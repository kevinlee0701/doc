#### Elasticsearch

#### 14 | Search API 概览

##### 本节知识点

- URI Search & Request Body Search
- 什么是搜索的相关性
- 如何衡量相关性

Search API

- URI Search : 在URL中使用查询参数
- Request Body Search : 使用Elasticsearch提供的，基于JSON格式的更加完备的Query Domain Specific Language (DSL)

指定查询的索引

| 语法                    | 范围              |
| ----------------------- | ----------------- |
| /_search                | 集群上所有的索引  |
| /index1/_search         | index1            |
| /index1,index-2/_search | index1 和 index2  |
| /index*/_search         | 以index开头的索引 |

URI查询

- 使用”q”，指定查询字符串
- “query string syntax”, KV 键值对

Code

```shell
curl -XGET
"http://elasticsearch:9200/kibana_sample_data_ecommerce/_search?q=customer_first_name:Eddie"
```

这里q用来表示查询内容，搜索叫做 Eddie 的客户

> Request Body Code

```shell
curl -XGET "http：//elasticsearch:9200/kibana_sample_data_ecommerce/_search" -H 'Content-Type:application/json' -d'
{
    "query":{  # 查询
        "match_all":{}  # 返回所有的文档
    }
}

# kibana_sample_data_ecommerce 需要操作的索引名
# _search 执行搜索的操作
```

> 搜索 Response

![image-20220208103438328](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220208103438328.png)

搜索的相关性Relevance

- 搜索是用户和搜索引擎的对话
- 用户关心的是搜索结果的相关性
  - 是否可以找到所有相关的内容
  - 有多少不相关的内容被返回了
  - 文档的打分是否合理
  - 结合业务需求，平衡结果排名

Web搜索

Page Rank 算法：不仅仅是内容，更重要的是内容的可信

度

电商搜索

搜索引擎扮演 - 销售的角色

- 提高用户购物体验
- 提升网站销售业绩
- 去库存

衡量相关性

Information Retrieval

- Precision （查准率）- 尽可能返回较少的无关文档
- Recall （查全率）- 尽量返回较多的相关文档
- Ranking - 是否能够按照相关度进行排序?

Precision & Recall

- Precision - True Positive / 全部返回的结果 (True and False Positives)
- Recall - True Positive / 所有应该返回的结果 (True positives + false Negtives)

使用Elasticsearch的查询和相关的参数改善搜索 的 Precision 和 Recall

##### demo

> Code

```shell
#URI Query
GET kibana_sample_data_ecommerce/_search?q=customer_first_name:Eddie
GET kibana*/_search?q=customer_first_name:Eddie
GET /_all/_search?q=customer_first_name:Eddie


#REQUEST Body
POST kibana_sample_data_ecommerce/_search
{
	"profile": true,
	"query": {
		"match_all": {}
	}
}
```

#### 15 | URI Search 详解

##### 本节知识点

- 指定字段查询泛查询
- 使用Profile参数
- Terms查询和Phrase查询的区别
- 如何对查询条件分组
- 逻辑操作符
- 通配符和近似匹配

URI Search - 通过URI query实现搜索

> Code

```shell
GET /movies/_search?q=2012&df=title&sort=year:desc&from=0&size=10&timeout=1s 
{
    “profile”: true
}
```

- q 指定查询语句，使用Query String Syntax
- df 默认字段，不指定时使用
- Sort 排序 / from 和 size 用于分页
- Profile 可以查看查询是如何被执行的

Query String Syntax (1)

- 指定字段 v.s 泛查询
  - q=title：2012 / q=2012
- Term v.s Phrase （单词和词组）
  - Beautiful Mind 等效于 Beautiful OR Mind
  - “Beautiful Mind”, 等效于 Beautiful AND Mind。Phrase 查询,还要求前后顺序保持一致
- 分组与引号
  - title：(Beautiful AND Mind)
  - title = “Beautiful Mind”

Query String Syntax (2)

- 布尔操作
  - AND / OR / NOT 或者 && / || / !
    - 必须大写
    - title：(matrix NOT reloaded)
- 分组
  - +: 表示must
  - -: 表示 must_not
  - title: (+matrix -reloaded)

Query String Syntax (3)

- 范围查询
  - 区间表示: [] 闭区间, {}开区间
    - year：｛2019 TO 2018］
    - year：［* TO 2018］

- 算数符号
  - year：>2010
  - year：(>2010 && <=2018)
  - year：(+>2010 +<=2018)

Query String Syntax (4)

- 通配符查询(通配符查询效率低，占用内存大，不建议使用。特别是放在最前面)
  - ？代表1个字符，* 代表0或多个字符
    - title : mi?d
    - title : be*
- 正则表达
  - title : [bt]oy
- 模糊匹配与近似查询
  - title：befutifl~1
  - title：”lord rings”〜2

本节小结

- URI查询的基本参数，分页，排序超时
- 指定字段与泛查询
- 查看查询的Profile
- 布尔条件
- 范围查询
- 模糊匹配

##### demo

带profile时，请注意观察返回值中的字段

Code

```shell
"type" : "TermQuery",
"description" : "title:2012",
```

Code

```shell
#基本查询
GET /movies/_search?q=2012&df=title&sort=year:desc&from=0&size=10&timeout=1s

#带profile
GET /movies/_search?q=2012&df=title
{
	"profile":"true"
}

#泛查询，正对_all,所有字段
GET /movies/_search?q=2012
{
	"profile":"true"
}

#指定字段
GET /movies/_search?q=title:2012&sort=year:desc&from=0&size=10&timeout=1s
{
	"profile":"true"
}

# 查找美丽心灵, Mind为泛查询
GET /movies/_search?q=title:Beautiful Mind
{
	"profile":"true"
}

# 泛查询
GET /movies/_search?q=title:2012
{
	"profile":"true"
}

#使用引号，Phrase查询
GET /movies/_search?q=title:"Beautiful Mind"
{
	"profile":"true"
}

#分组，Bool查询
GET /movies/_search?q=title:(Beautiful Mind)
{
	"profile":"true"
}

#布尔操作符
# 查找美丽心灵
GET /movies/_search?q=title:(Beautiful AND Mind)
{
	"profile":"true"
}

# 查找美丽心灵
GET /movies/_search?q=title:(Beautiful NOT Mind)
{
	"profile":"true"
}

# 查找美丽心灵
GET /movies/_search?q=title:(Beautiful %2BMind)
{
	"profile":"true"
}

#范围查询 ,区间写法
GET /movies/_search?q=title:beautiful AND year:[2002 TO 2018%7D
{
	"profile":"true"
}

#通配符查询
GET /movies/_search?q=title:b*
{
	"profile":"true"
}

#模糊匹配&近似度匹配
GET /movies/_search?q=title:beautifl~1
{
	"profile":"true"
}

GET /movies/_search?q=title:"Lord Rings"~2
{
	"profile":"true"
}
```

#### 16 | Request Body & Query DSL 介绍

##### 本节知识点

- 分页/排序
- Source Filtering
- Match 查询和 Match Phrase 查询
- 调整 Precision & Recall

Request Body Search

- 将查询语句通过HTTP Requedt Body发送给Elasticsearch
- Query DSL

Code

```shell
POST /movies,404_idx/_search?ignore_unavailable=true
{
    "profile": true,
    "query": {
        "match_all: {}
    }
}
```

分页

Code

```shell
POST /kibana_sample_data_ecommerce/_search
{
    "from":10, 
    "size":20, 
    "query":{
        "match_all": {} 
    }
}
```

- From从0开始，默认返回10个结果
- 获取靠后的翻页成本较高

排序

Code



```shell
GET kibana_sample_data_ecommerce/_search
{
    "sort": [{"order_date":"desc"}] ,
    "from": 10,
    "size": 5,
    "query": {
        "match_all": {}
    }
}
```

1. 最好在“数字型”与“日期型”字段上排序
2. 因为对于多值类型或分析过的字段排序，系统会选一个值，无法得知该值

source filtering



Code



```shell
GET kibana_sample_data_ecommerce/_search
{ 
	"_source":["order_date", "order_date", "category.keyword"],
	"from": 10,
	"size": 5,
	"query": { 
		"match_all": {}
	}
}
```

- 如果_source没有存储，那就只返回匹配的文档的元数据
- _source支持使用通配符, _source[“name”, “desc*“]

脚本字段



Code



```
GET kibana_sample_data_ecommerce/_search
{
    "script_fields": {
        "new_field": {
            "script": {
                "lang": "painless",
                "source":"doc['order_date'].value+'hello'"
            }
        }
    },
    "from": 10,
    "size": 5,
    "query": {
    	"match_all": {}
    }
}
```

- 用例：订单中有不同的汇率，需要结合汇率对，订单价格进行排序

使用查询表达式 - Match



Code



```
GET /comments/_doc/_search
{
	"query":{
		"match":{
			"comment":"Last Cfirismas"
		}
	}
}
```



Code



```
GET /comments/_doc/_search
{
	"query":{
		"match":{
			"comment": { 
				"query1': "Last Cfirismas",
				"operator":"AND"
			}	
		}
	}
}
```

短语搜索 - Match Phrase



Code



```
GET /comments/_doc/_search
{
    "query":{
    	"match_phrase":{
    		"comment":{
    			"query":"Song Last Chrismas",
    			"slop": 1
    		}
    	}
    }
}
```

##### demo

- Request Body DSL和相关参数



Code



```shell
#ignore_unavailable=true，可以忽略尝试访问不存在的索引“404_idx”导致的报错
#查询movies分页
POST /movies,404_idx/_search?ignore_unavailable=true
{
  "profile": true,
	"query": {
		"match_all": {}
	}
}

POST /kibana_sample_data_ecommerce/_search
{
  "from":10,
  "size":20,
  "query":{
    "match_all": {}
  }
}


#对日期排序
POST kibana_sample_data_ecommerce/_search
{
  "sort":[{"order_date":"desc"}],
  "query":{
    "match_all": {}
  }

}

#source filtering
POST kibana_sample_data_ecommerce/_search
{
  "_source":["order_date"],
  "query":{
    "match_all": {}
  }
}


#脚本字段
GET kibana_sample_data_ecommerce/_search
{
  "script_fields": {
    "new_field": {
      "script": {
        "lang": "painless",
        "source": "doc['order_date'].value+'hello'"
      }
    }
  },
  "query": {
    "match_all": {}
  }
}


POST movies/_search
{
  "query": {
    "match": {
      "title": "last christmas"
    }
  }
}

POST movies/_search
{
  "query": {
    "match": {
      "title": {
        "query": "last christmas",
        "operator": "and"
      }
    }
  }
}

POST movies/_search
{
  "query": {
    "match_phrase": {
      "title":{
        "query": "one love"

      }
    }
  }
}

POST movies/_search
{
  "query": {
    "match_phrase": {
      "title":{
        "query": "one love",
        "slop": 1

      }
    }
  }
}
```

#### 17 | Query String & Simple Query String 查询

##### 本节知识点

- Query String
- Simple Query String

Query String Query

- 类似UR Query

Simple Query String Query

- 类似Query String，但是会忽略错误的语法, 同时只支持部分查询语法
- 不支持AND OR NOT,会当作字符串处理
- Term之间默认的关系是OR,可以指定 Operator

支持部分逻辑

+替代AND

|替代OR

-替代NOT

##### Demo



Code



```shell
PUT /users/_doc/1
{
  "name":"Ruan Yiming",
  "about":"java, golang, node, swift, elasticsearch"
}

PUT /users/_doc/2
{
  "name":"Li Yiming",
  "about":"Hadoop"
}

POST users/_search
{
  "query": {
    "query_string": {
      "default_field": "name",
      "query": "Ruan AND Yiming"
    }
  }
}

POST users/_search
{
  "query": {
    "query_string": {
      "fields":["name","about"],
      "query": "(Ruan AND Yiming) OR (Java AND Elasticsearch)"
    }
  }
}

#Simple Query 默认的operator是 Or
POST users/_search
{
  "query": {
    "simple_query_string": {
      "query": "Ruan AND Yiming",
      "fields": ["name"]
    }
  }
}

POST users/_search
{
  "query": {
    "simple_query_string": {
      "query": "Ruan Yiming",
      "fields": ["name"],
      "default_operator": "AND"
    }
  }
}

GET /movies/_search
{
	"profile": true,
	"query":{
		"query_string":{
			"default_field": "title",
			"query": "Beafiful AND Mind"
		}
	}
}

# 多fields
GET /movies/_search
{
	"profile": true,
	"query":{
		"query_string":{
			"fields":[
				"title",
				"year"
			],
			"query": "2012"
		}
	}
}

GET /movies/_search
{
	"profile":true,
	"query":{
		"simple_query_string":{
			"query":"Beautiful +mind",
			"fields":["title"]
		}
	}
}
```

#### 18 | Dynamic Mapping和常见字段类型

##### 本节知识点

- Mapping & Dynamic Mapping
- 字段类型自动识别
- 控制Mapping的Dynamic属性

什么是Mapping

Mapping类似数据库中的schema的定义，作用如下

- 定义索引中的字段的名称
- 定义字段的数据类型，例如字符串，数字，布尔
- 字段，倒排索引的相关配置，(Analyzed or Not Analyzed, Analyzer)

Mapping会把JSON文档映射成Lucene所需要的扁平格式

一个Mapping属于一个索引的Type

- 每个文档都属于一个Type
- 一个Type有一个Mapping定义
- 7.0开始，不需要在Mapping定义中指定type信息

字段的数据类型

- 简单类型
  - Text / Keyword
  - Date
  - Integer / Floating
  - Boolean
  - IPv4 & IPv6
- 复杂类型 - 对象和嵌套对象
  - 对象类型 / 嵌套类型
- 特殊类型
  - geo_point & geo_shape / percolator

什么是 Dynamic Mapping

在写入文档时候，如果索引不存在，会自动创建索引

Dynamic Mapping 的机制，使得我们无须手动定义 Mappings. Elasticsearch 会自动根据文档信息，推算出字段类型

但是有时候推算的信息不对，例如地址位置信息

当类型设置不对时，会导致一些功能无法正常使用，例如 Range 查询

类型的自动识别

| JSON类型 | Elasticsearch 类型                                           |
| -------- | ------------------------------------------------------------ |
| 字符串   | • 匹配日期格式，设置成Date • 配置数字设置为float或者long,该选项默认关闭 • 设置为Text,并且增加keyword子字段 |
| 布尔值   | boolean                                                      |
| 浮点数   | float                                                        |
| 整数     | long                                                         |
| 对象     | Object                                                       |
| 数组     | 由第一个非空数值的类型所决定                                 |
| 空值     | 忽略                                                         |

Demo

- 写入文档，看Elasticsearch会如何推导数据类型
  - 数字用引号，默认当TEXT
  - 日期格式会推导成Date
- 有一些类型会推导出错，例如地理位置信息。

能否更改Mapping的字段类型

两种情况

1. 新增加字段
   - Dynamic设为true时，一旦有新增字段的文档写入，Mapping也同时被更新
   - Dynamic设为false, Mapping不会被更新，新增字段的数据无法被索引, 但是信息会出现在_source中
   - Dynamic设置成Strict，文档写入失败
2. 对已有字段，一旦已经有数据写入，就不再支持修改字段定义
   - Lucene实现的倒排索引，一旦生成后，就不允许修改
3. 如果希望改变字段类型，必须Reindex API，重建索引

原因

1. 如果修改了字段的数据类型，会导致已被索引的属于无法被搜索
2. 但是如果是增加新的字段，就不会有这样的影响

控制 Dynamic Mappings

|               | “true” | “false” | “strict” |
| ------------- | ------ | ------- | -------- |
| 文档可索引    | YES    | YES     | NO       |
| 字段可索引    | YES    | NO      | NO       |
| Mapping被更新 | YES    | NO      | NO       |

- 当dynamic被设置成false时候，存在新增字段的数据写入，该数据可以被索引, 但是新增字段被丢弃
- 当设置成Strict模式时候，数据写入直接出错

##### Demo

- 设置 Dynamic True
- 设置 Dynamic False
- 设置 Dynamic Strict



Code



```shell
#写入文档，查看 Mapping
PUT mapping_test/_doc/1
{
  "firstName":"Chan",
  "lastName": "Jackie",
  "loginDate":"2018-07-24T10:29:48.103Z"
}

#查看 Mapping文件
GET mapping_test/_mapping

#Delete index
DELETE mapping_test

#dynamic mapping，推断字段的类型
PUT mapping_test/_doc/1
{
    "uid" : "123",
    "isVip" : false,
    "isAdmin": "true",
    "age":19,
    "heigh":180
}

#查看 Dynamic
GET mapping_test/_mapping

#默认Mapping支持dynamic，写入的文档中加入新的字段
PUT dynamic_mapping_test/_doc/1
{
  "newField":"someValue"
}

#该字段可以被搜索，数据也在_source中出现
POST dynamic_mapping_test/_search
{
  "query":{
    "match":{
      "newField":"someValue"
    }
  }
}

#修改为dynamic false
PUT dynamic_mapping_test/_mapping
{
  "dynamic": false
}

#新增 anotherField
PUT dynamic_mapping_test/_doc/10
{
  "anotherField":"someValue"
}

#该字段不可以被搜索，因为dynamic已经被设置为false
POST dynamic_mapping_test/_search
{
  "query":{
    "match":{
      "anotherField":"someValue"
    }
  }
}

get dynamic_mapping_test/_doc/10

#修改为strict
PUT dynamic_mapping_test/_mapping
{
  "dynamic": "strict"
}

#写入数据出错，HTTP Code 400
PUT dynamic_mapping_test/_doc/12
{
  "lastField":"value"
}

DELETE dynamic_mapping_tes
```

#### 19 | 显式Mapping设置与常见参数介绍

##### 本节知识点

- 如何显示定义Mapping
- Mapping定义的一些参数

如何显示定义一个Mapping



Code



```json
PUT movies
{
    "mappings": {
    	//define your mappings here
    }
}
```

自定义 Mapping 的一些建议

可以参考API手册，纯手写

为了减少输入的工作量，减少出错概率，可以依照以下步骤

- 创建一个临时的index，写入一些样本数据
- 通过访问Mapping API获得该临时文件的动态Mapping定义
- 修改后用，使用该配置创建你的索引
- 删除临时索引

控制当前字段是否被索引

Index -控制当前字段是否被索引。默认为true。如果设置成false，该字段不可被搜索



Code



```json
PUT users
{
	"mapping" : {
		"properties" : {
			"firstName" : {
				"type" : "text"
			},
			"mobile" : {
				"type" : "text",
				"index" : false
			}
		}
	}
}
```

Index Options

四种不同级别的Index Options配置，可以控制倒排索引记录的内容

- docs - 记录 doc id
- freqs - 记录 doc id 和 term frequencies
- positions - 记录 doc id / term frequencies / term position
- offsets - doc id / term frequencies / term posistion / character offects

==Text类型默认记录postions, 其他默认为docs==

记录内容越多，占用存储空间越大



Code



```json
PUT users
{
	"mapping" : {
		"properties" : {
			"firstName" : {
				"type" : "text"
			},
			"mobile" : {
				"type" : "text",
				"index" : false
			},
			"bio" : {
				"type" : "text",
				"index_options" : "offsets"
			}
		}
	}
}
```

null_value

需要对Null值实现搜索, 只有Keyword类型支持设定Null_Value



Code



```json
GET users/_search?q=mobile:NULL

PUT users
{
	"mapping" : {
		"properties" : {
			"firstName" : {
				"type" : "text"
			},
			"mobile" : {
				"type" : "text",
				"null_value" : "NULL"
			}
		}
	}
}
```

copy_to 设置

- _all 在 7 中被 copy_to 所替代
- 满足一些特定的搜索需求
- copy_to 将字段的数值拷贝到目标字段，实现类似 _all 的作用
- copy_to 的目标字段不出现在_source中



Code



```json
PUT users
{
	"mapping" : {
		"properties" : {
			"firstName" : {
				"type" : "text"
				"copy_to" : "fullName"
			},
			"mobile" : {
				"type" : "text",
				"copy_to" : "fullName"
			}
		}
	}
}

GET users/_search?q=fullName:(Ruan YiMing)
```

数组类型

Elasticsearch中不提供专门的数组类型。但是任何字段，都可以包含多个相同类类型的数值

Code

```json
put users/_doc/1
{
	"name":"onebird",
	"interests":"reading"
}

put users/_doc/2
{
	"name":"twobird",
	"interests":["reading", "music"]
}
```

##### demo

Code

```json
#设置 index 为 false
DELETE users
PUT users
{
    "mappings" : {
        "properties" : {
            "firstName" : {
                "type" : "text"
            },
            "mobile" : {
                "type" : "text",
                "index": false
            }
        }
    }
}

PUT users/_doc/1
{
  "firstName":"Ruan",
  "mobile": "12345678"
}

POST /users/_search
{
  "query": {
    "match": {
      "mobile":"12345678"
    }
  }
}

#设定Null_value
DELETE users
PUT users
{
    "mappings" : {
      "properties" : {
        "firstName" : {
          "type" : "text"
        },
        "mobile" : {
          "type" : "keyword",
          "null_value": "NULL"
        }
      }
    }
}

PUT users/_doc/1
{
  "firstName":"Ruan",
  "mobile": null
}

PUT users/_doc/2
{
  "firstName":"Ruan2"
}

GET users/_search
{
  "query": {
    "match": {
      "mobile":"NULL"
    }
  }
}

#设置 Copy to
DELETE users
PUT users
{
  "mappings": {
    "properties": {
      "firstName":{
        "type": "text",
        "copy_to": "fullName"
      },
      "lastName":{
        "type": "text",
        "copy_to": "fullName"
      }
    }
  }
}

PUT users/_doc/1
{
  "firstName":"Ruan",
  "lastName": "Yiming"
}

GET users/_search?q=fullName:(Ruan Yiming)

POST users/_search
{
  "query": {
    "match": {
       "fullName":{
        "query": "Ruan Yiming",
        "operator": "and"
      }
    }
  }
}

#数组类型
PUT users/_doc/1
{
  "name":"onebird",
  "interests":"reading"
}

PUT users/_doc/1
{
  "name":"twobirds",
  "interests":["reading","music"]
}

POST users/_search
{
	"query": {
		"match_all": {}
	}
}

GET users/_mapping
```

补充阅读

- Mapping Parameters https://www.elastic.co/guide/en/elasticsearch/reference/7.1/mapping-params.html

#### 20 | 多字段特性及配置自定义Analyzer

##### 多字段类型

多字段特性

- 厂商名字实现精确匹配
  - 增加一个keyword字段
- 使用不同的analyzer
  - 不同语言
  - pinyin字段的搜索
  - 还支持为搜索和索引指定不通的analyzer



Code

```json
PUT products
{
  "mappings":{
    "properties"{
      "company":{
        "type":"text",
        "fields":{
          "keyword":{
            "type":"keyword",
            "ignore_above":256
          }
        }
      },
      "comment":{
        "type":"text",
        "fields":{
          "english_comment":{
            "type":"text",
            "analyzer":"english",
            "search_analyzer":"english"
          }
        }
      }
    }
  }
}
```

Exact Values v.s Full Text

- Exact Value：包括数字/日期/具体一个字符串(例如“Apple Store”);
  - Elasticseach 中的 keyword
- 全文本，非结构化的文本数据;
  - Elasticsearch 中的 text

Exact Values不需要被分词

Elasticsearch为每一个字段创建一个倒排索引

- Exact Value在索引时，不需要做特殊的分词处理

自定义分词

当Elasticsearch自带的分词器无法满足时，可以自定义分词器。通过自组合不同的组 件实现

- Character Filter
- Tokenizer
- Token Filter

Character Filters

在Tokenizer之前对文本进行处理，例如增加删除及替换字符。可以配置 多个 Character Filters。会影响 Tokenizer 的 position 和 offset 信息

一些自带的 Character Filters

- HTML strip - 出除 html 标签
- Mapping - 字符串替换
- Pattern replace - 正则匹配替换

Tokenizer

- 将原始的文本按照一定的规则，切分为词(term or token)
- Elasticsearch 内置的 Tokenizers, 例如 whitespace / standard / uax_url_email / pattern / keyword / path hierarchy
- 可以用Java开发插件，实现自己的Tokenizer

Token Filters

- 将Tokenizer输出的单词（term）,进行增加，修改，删除
- 自带的 Token Filters，例如 Lowercase / stop / synonym （添加近义词）

设置一个 Custom Analyzer



Code



```json
{
  "settings": {
    "analysis": {
      "analyzer": {
        "my_english": {
          "type": "english",
          "stem_exclusion": [ "organization", "organizations" "stopwords":[
            "a", "an", "and", "are", "as", "at", "be", "but", "by", "for", 
            "if", "in", "into", "is", "it", "of", "on", "or", "such", "that", 
            "the", "their", "then", "there", "these", "they", "this", "to", 
            "was", "will", "with"
            ]
          }
        }
      }
    }
  }
}
```

demo



Code



```json
PUT logs/_doc/1
{"level":"DEBUG"}

GET /logs/_mapping

POST _analyze
{
  "tokenizer":"keyword",
  "char_filter":["html_strip"],
  "text": "<b>hello world</b>"
}


POST _analyze
{
  "tokenizer":"path_hierarchy",
  "text":"/user/ymruan/a/b/c/d/e"
}


#使用char filter进行替换
POST _analyze
{
  "tokenizer": "standard",
  "char_filter": [
      {
        "type" : "mapping",
        "mappings" : [ "- => _"]
      }
    ],
  "text": "123-456, I-test! test-990 650-555-1234"
}

#char filter 替换表情符号
POST _analyze
{
  "tokenizer": "standard",
  "char_filter": [
      {
        "type" : "mapping",
        "mappings" : [ ":) => happy", ":( => sad"]
      }
    ],
    "text": ["I am felling :)", "Feeling :( today"]
}

# white space and snowball
GET _analyze
{
  "tokenizer": "whitespace",
  "filter": ["stop","snowball"],
  "text": ["The gilrs in China are playing this game!"]
}


# whitespace与stop
GET _analyze
{
  "tokenizer": "whitespace",
  "filter": ["stop","snowball"],
  "text": ["The rain in Spain falls mainly on the plain."]
}


#remove 加入lowercase后，The被当成 stopword删除
GET _analyze
{
  "tokenizer": "whitespace",
  "filter": ["lowercase","stop","snowball"],
  "text": ["The gilrs in China are playing this game!"]
}

#正则表达式
GET _analyze
{
  "tokenizer": "standard",
  "char_filter": [
      {
        "type" : "pattern_replace",
        "pattern" : "http://(.*)",
        "replacement" : "$1"
      }
    ],
    "text" : "http://www.elastic.co"
}
```

#### 21 | Index Template 和 Dynamic Template

##### Index Template

管理很多的索引

集群上的索引会越来越多，例如，你会为你的日志每天创建一个索引

- 使用多个索引可以让你更好的管理你的数据，提高性能，例如 logs-2019-05-01，logs-2019-05-02

什么是 Index Template

Index Templates -帮助你设定Mappings和Settings,并按照一定的规则，自动匹配到新创建的索引之上

- 模版仅在一个索引被新创建时，才会产生作用。修改模版不会影响已创建的索引
- 你可以设定多个索引模版，这些设置会被“merge”在一起
- 你可以指定“order”的数值，控制“merging”的过程

两个 Index Templates

template_default



Code



```json
PUT _template/template_default
{
  "index_patterns": ["*"],
  "order": 0,
  "version": 1,
  "setting": {
    "number_of_shards": 1,
    "number_of_replicas": 1
  }
}
```

template_test



Code



```json
PUT /_template/template_test
{
  "index_patterns": ["test*"],
  "order": 1,
  "settings": {
    "number_of_shards": 1,
    "number_of_replicas": 2
  }
  "mappings": {
    "date_detection": false,
    "numeric_detection": true
  }
}
```

Index Template的工作方式

当一个索引被新创建时

- 应用 Elasticsearch 默认的 settings 和 mappings
- 应用order数值低的Index Template中的设定
- 应用order高的Index Template中的设定，之前的设定会被覆盖
- 应用创建索引时，用户所指定的Settings和Mappings,并覆盖之前模版中的设定

##### Demo

- 创建 2 个 Index Templates
- 查看根据名字查看Template
- 查看所有 templates, —template/*
- 创建一个临时索引，查看replica和数据类型推断
- 将索引名字设为能Index Template匹配时，查看所生成的Index的mappings和Settings



Code



```shell
#数字字符串被映射成text，日期字符串被映射成日期
PUT ttemplate/_doc/1
{
	"someNumber":"1",
	"someDate":"2019/01/01"
}

GET ttemplate/_mapping

#Create a default template
PUT _template/template_default
{
  "index_patterns": ["*"],
  "order" : 0,
  "version": 1,
  "settings": {
    "number_of_shards": 1,
    "number_of_replicas":1
  }
}

PUT /_template/template_test
{
    "index_patterns" : ["test*"],
    "order" : 1,
    "settings" : {
    	"number_of_shards": 1,
        "number_of_replicas" : 2
    },
    "mappings" : {
    	"date_detection": false,
    	"numeric_detection": true
    }
}

#查看template信息
GET /_template/template_default
GET /_template/temp*

#写入新的数据，index以test开头
PUT testtemplate/_doc/1
{
	"someNumber":"1",
	"someDate":"2019/01/01"
}
GET testtemplate/_mapping
GET testtemplate/_settings

PUT testmy
{
	"settings":{
		"number_of_replicas":5
	}
}

PUT testmy/_doc/1
{
  "key":"value"
}

GET testmy/_settings

#清除测试数据
DELETE testmy
DELETE /_template/template_default
DELETE /_template/template_test
```

---

##### Dynamic Template

什么是 Dynamic Template

根据Elasticsearch识别的数据类型，结合字段名称，来动态设定字段类型

- 所有的字符串类型都设定成Keyword, 或者关闭keyword字段
- is开头的字段都设置成boolean
- long_开头的都设置成long类型

Dynamic Template



Code



```shell
PUT my_test_index
{
  "mapping": {
    "dynamic_templates": [
      {
        "full_name": {
          "path_match": "name.*",
          "path_unmatch": "*.middle",
          "mapping": {
            "type": "text",
            "copy_to": "full_name"
          }
        }
      }
    ]
  }
}
```

- Dynamic Tempate是定义在在某个索引的Mapping中
- Template有一个名称
- 匹配规则是一个数组
- 为匹配到字段设置Mapping

匹配参数规则

留意数组中的顺序

Code

```shell
put my_index
{
  "mapping": {
    "dynamic_template": [
      {
        "strings_as_boolean": {
          "match_mapping_type": "string",
          "match": "is*",
          "mapping": {
            "type": "boolean"
          }
        }
      },
      {
        "strings_as_keywords": {
          "match_mapping_type": "string",
          "mapping": {
            "type": "keyword"
          }
        }
      }
    ]
  }
}
```

- match_mapping_type:匹配自动识别的字段类型, 如 string, boolean等
- match, unmatch：匹配字段名
- path_match, path_unmatch

##### Demo

- 根据Type和字段名
- 根据Path匹配



Code



```
#Dynaminc Mapping 根据类型和字段名
DELETE my_index
PUT my_index/_doc/1
{
  "firstName":"Ruan",
  "isVIP":"true"
}

GET my_index/_mapping

DELETE my_index
PUT my_index
{
  "mappings": {
    "dynamic_templates": [
            {
        "strings_as_boolean": {
          "match_mapping_type":   "string",
          "match":"is*",
          "mapping": {
            "type": "boolean"
          }
        }
      },
      {
        "strings_as_keywords": {
          "match_mapping_type":   "string",
          "mapping": {
            "type": "keyword"
          }
        }
      }
    ]
  }
}


#Dynaminc Mapping 根据结合路径
DELETE my_index
PUT my_index
{
  "mappings": {
    "dynamic_templates": [
      {
        "full_name": {
          "path_match":   "name.*",
          "path_unmatch": "*.middle",
          "mapping": {
            "type":       "text",
            "copy_to":    "full_name"
          }
        }
      }
    ]
  }
}

PUT my_index/_doc/1
{
  "name": {
    "first":  "John",
    "middle": "Winston",
    "last":   "Lennon"
  }
}

GET my_index/_search?q=full_name:John
DELETE my_index
```

相关阅读

- Index Templates https://www.elastic.co/guide/en/elasticsearch/reference/7.1/indices-templates.html
- Dynamic Template https://www.elastic.co/guide/en/elasticsearch/reference/7.1/dynamic-mapping.html

#### 22 | Elasticsearch聚合分析简介

什么是聚合(Aggregation)

Elasticsearch除搜索以外，提供的针对ES数据进行统计分析的功能；具有实时性高，而Hadoop (T+1)

通过聚合，我们会得到一个数据的概览，是分析和总结全套的数据，而不是寻找单个文档；例如尖沙咀和香港岛的客房数量，不同的价格区间，可预定的经济型酒店和五星级酒店的数量

高性能，只需要一条语句，就可以从Elasticsearch得到分析结果；无需在客户端自己去实现分析逻辑

Kibana可视化报表-聚合分析

- 客户的地理位置分布
- 订单的增长情况

集合的分类

- Bucket Aggregation - —些列满足特定条件的文档的集合【相当于group】
- Metric Aggregation - 一些数学运算，可以对文档字段进行统计分析【相当于 count，sum函数 】
- Pipeline Aggregation - 对其他的聚合结果进行二次聚合
- Matrix Aggregration -支持对多个字段的操作并提供一个结果矩阵

Bucket & Metric

Metric - 一些系列的统计方法, 类比 mysql count

Bucket - 一组满足条件的文档，类比 mysql group

Bucket

Elasticsearch提供了很多类型的Bucket,帮助 你用多种方式划分文档, 例如 Term & Range （时间/年龄区间/地理位 置）

Metric

Metric会基于数据集计算结果，除了支持在字段上进行计算，同样也支持在脚本；例如(painless script)产生的结果之上进行计算

大多数Metric是数学计算，仅输出一个值；例如 min / max / sum / avg / cardinality

部分metric支持输出多个数值；例如 stats / percentiles / percentile_ranks

aggs 聚合的模板



Code



```shell
GET 127.0.0.1/mytest/doc/_search
{
    "query": { ... },
    "size": 0,
    "aggs": {
        "custom_name1": {  //aggs后面接著的是一个自定义的name
            "桶": { ... }  //再来才是接桶
        },
        "custom_name2": {  //一个aggs裡可以有很多聚合
            "桶": { ... }
        },
        "custom_name3": {
            "桶": {
               .....
            },
            "aggs": {  //aggs可以嵌套在别的aggs裡面
                "in_name": { //记得使用aggs需要先自定义一个name
                    "桶": { ... } //in_name的桶作用的文档是custom_name3的桶的结果
                }
            }
        }
    }
}
```



```shell
GET kibana_sample_data_flights/_search
{
  "aggs": {
    "flight_dest": {
      "terms": {
        "field": "DestCountry"
      }
      ,
      "aggs": {
        "avg_price": {
          "avg": {
            "field": "AvgTicketPrice"
          }
        }
      }
    }
   
  }
}
```



一个 Bucket 的例子

查看航班目的地的统计信息



Code



```
GET kibana_sample_date_flights/_search
{
  "size": 0,
  "aggs": {
    "flight_dest": {
      "terms": {
        "field": "DestCountry"
      }
    }
  }
}
```

加入 Metrics

查看航班目的地的统计信息，增加均价，最高最低价格



Code



```
GET kibana_sample_data_flights/_search
{
  "size": 0,
  "aggs": {
    "flight_dest": {
      "terms": {
        "field": "DestCountry"
      },
      "aggs": {
        "average_price": {
          "avg": {
            "field": "AvgTicketPrice"
          }
        }
      }
    }
  }
}
```

嵌套

查看航班目的地的统计信息，平均票价，以及天气状况



Code



```
GET kibana_sample_data_flights/_search
{
  "size": 0,
  "aggs": {
    "flight_dest": {
      "terms": {
        "field": "DestCountry"
      },
      "aggs": {
        "average_price": {
          "avg": {
            "field": "AvgTicketPrice"
          }
        },
        "weather": {
          "terms": {
            "field": "DestWeather"
          }
        }
      }
    }
  }
}
```

本章回顾

- Bucket -满足一定条件的文档集合
- Metric -对数据集进行计算
- 通过Elasticsearch的聚合功能，可以实现对数据进行统计分析
  - Bucket Aggregation - Terms & Range
  - Metrics Aggregation - max, min, avg
- 聚合支持嵌套

demo



Code



```
#按照目的地进行分桶统计
GET kibana_sample_data_flights/_search
{
	"size": 0,
	"aggs":{
		"flight_dest":{
			"terms":{
				"field":"DestCountry"
			}
		}
	}
}

#查看航班目的地的统计信息，增加平均，最高最低价格
GET kibana_sample_data_flights/_search
{
	"size": 0,
	"aggs":{
		"flight_dest":{
			"terms":{
				"field":"DestCountry"
			},
			"aggs":{
				"avg_price":{
					"avg":{
						"field":"AvgTicketPrice"
					}
				},
				"max_price":{
					"max":{
						"field":"AvgTicketPrice"
					}
				},
				"min_price":{
					"min":{
						"field":"AvgTicketPrice"
					}
				}
			}
		}
	}
}

#价格统计信息+天气信息
GET kibana_sample_data_flights/_search
{
	"size": 0,
	"aggs":{
		"flight_dest":{
			"terms":{
				"field":"DestCountry"
			},
			"aggs":{
				"stats_price":{
					"stats":{
						"field":"AvgTicketPrice"
					}
				},
				"wather":{
				  "terms": {
				    "field": "DestWeather",
				    "size": 5
				  }
				}
			}
		}
	}
}
```

相关阅读

- https://www.elastic.co/guide/en/elasticsearch/reference/7.1/search-aggregations.html

#### 23 | 第一部分总结&测试

##### 第一部分总结与回顾：产品与使用场景

Elasticsearch是一个开源的分布式搜索与分析引擎，提供了近实时搜索和聚合两大功能

Elastic Stack 包括 Elasticsearch, Kibana, Logstash, Beats 等一系列产品。Elasticsearch是核心引擎，提供了海量数据存储，搜索和聚合的能力。Beats是轻量的 数据采集器，Logstash用来做数据转换，Kibana则提供了丰富的可视化展现与分析的功 能。

Elastic Stack主要被广泛使用于：搜索，日志管理，安全分析，指标分析，业务分析， 应用性能监控等多个领域

Elastic Stack开源了X-Pack在内的相关代码。作为商业解决方案，X-Pack的部分功能 需要收费。Elastic公司从6.8和7.1开始，Security功能也可以免费使用

相比关系型数据库，Elasticsearch提供了如模糊查询，搜索条件的算分第等关系型数据库所不擅长的功能，但是在事务性等方面，也不如关系型数据库来的强大。因此，在实际生产环境中，需要考虑具体业务要求，综合使用

##### 第一部分总结与回顾：基本概念

一个Elasticsearch集群可以运行在单节点上，也支持运行在多个服务器上，实现数据和服务的水平扩展

从逻辑角度看，索引是一些具有相似结构的文档的集合

物理角度看，分片是一个Lucene的实例。分片存储了索引的具体数据，分片可以分布在不同的节点 之上。副本分片除了提高数据的可靠性，还能一定程度提升集群查询的性能

Elasticsearch的文档可以是任意的JSON格式的数据

将文档写进Elasticseach的过程叫索引（indexing）

Elasticsearch 提供了 REST API 和 Transport API 两种方式，建议使用 REST API

##### 第一部分总结与回顾：搜索和Aggregation

Precosion指除了相关的结果，还返回了多少不相关的结果

Recall 一衡量有多少相关的结果，实际上并没有返回

精确值包括：数字，日期和某些具体的字符串

全文本：是需要被检索的非结构文本

Analyis是将文本转换成倒排索引中的Terms的过程

Elasticsearch 的 Analyzer 是 Char_filter -> Tokenizer -> Token Filter的过程

要善于利用.analyze API去测试Analyzer

Elasticsearch 搜索支持 UR Search 和 REST Body 两种方式

Elasticsearch 提供了Bucket / Metric / Pipeline / Matrix 四种方式的聚合

##### 第一部分总结与回顾：文档CRUD与Index Mapping

除了 CRUD操作外，Elasticsearch还提供了 bulk, mget和mseach等操作。从性能的角度 上说，建议使用，以提升性能。但是，单次操作的数据量不要过大，以免引发性能问题

每个索引都有一个Mapping定义。包含文档的字段及类型，字段的Analyzer的相关配置

Mapping可以被动态的创建，为了避免一些错误的类型推算或者满足你特定的需求，可以显示 的定义Mapping

Mapping可以动态创建，也可以显示定义。你可以在Mapping中定制Analyzer

你可以为字段指定定制化的analyzer，也可以为查询字符串指定search_analyzer

Index Template可以定义Mapping和Settings,并自动的应用到新创建的索引之上，建议要 合理的使用Index Template

Dynamic Template支持在具体的索引上指定规则，为新增加的字段指定相应的Mappings

##### 第一部分自我测试（一）

> **问题**

1. 判断题：ES支持使用HTTP PUT写入新文档，并通过Elasticsearch生成文档Id
2. 判断题：Update 一个文档，需要使用HTTP PUT
3. 判断题：Index -个已存在的文档，旧的文档会先被删除，新的文档再被写入，同时版本号加1
4. 尝试描述创建一个新的文档到一个不存在的索引中，背后会发生一些什么？
5. ES7中的合法的type是什么？
6. 精确值和全文的本质区别是什么？
7. Analyzer由哪几个部分组成？

> **答案**

1. 错，需要用POST命令创建。
2. 错，Update文档，使用POST, PUT只能用来做Index或者Create
3. 对
4. 默认情况下，会创建相应的索引，并且自己设置Mapping,当然，实际情况还是要看 是否有合适的Index Template
5. _doc
6. 精确值不会被Analyzer分词，全文本会
7. 三部分，Character Filter + Tokenizer + Token Filter

##### 第一部分自我测试（二）

> **问题**

1. 尝试描述match和match_phrase的区别
2. 如果你希望match_phrase匹配到更多结果，你应该配置查询中什么参数？
3. 如果Mapping的dynamic设置成“strict”,索引一个包含新增字段的文档时会发生什么?
4. 如果Mapping的dynamic设置成“false”,索引一个包含新增字段的文档时会发生什么？
5. 判断：可以把一个字段的类型从“integer”改成“long”，因为这两个类型是兼容的
6. 判断：你可以在Mapping文件中为indexing和searching指定不同的analyzer
7. 判断：字段类型为Text的字段，一定可以被全文搜索

> **答案**

1. Match中的terms之间是。「的关系，Match Phrase的terms之间是and的关系, 并且term之间位置关系也影响搜索的结果
2. slop
3. 直接报错
4. 文档被索引，新的字段在_source中可见。但是该字段无法被搜索
5. 错。字段类型修改，需要重新reindex
6. 对。可以在Mapping中为index和search指定不同的analyizer
7. 错。可以通过为text类型的字段指定Not Indexed，使其无法被搜索

#### 24 | 基于词项和基与全文的搜索

##### 基于 Term 的查询

Term 的重要性：Term 是表达语意的最小单位。搜索和利用统计语言模型进行自然语言处理都需要处理 Term

Term 的特点：

- Term Level Query: Term Query / Range Query / Exists Query / Prefix Query /Wildcard Query
- 在 ES 中，Term 查询，对输入**不做分词**。会将输入作为一个整体，在倒排索引中查找准确的词项，并且使用相关度算分公式为每个包含该词项的文档进行**相关度算分** – 例如“Apple Store”
- 可以通过 Constant Score 将查询转换成**一个 Filtering，避免算分，并利用缓存，**提高性能

关于 Term 查询的例子



Code



```json
DELETE products
PUT products
{"settings": { "number_of_shards": 1 } }

POST /products/_bulk
{ "index": { "_id": 1 }}
{ "productID" : "XHDK-A-1293-#fJ3","desc":"iPhone" }
{ "index": { "_id": 2 }}
{ "productID" : "KDKE-B-9947-#kL5","desc":"iPad" }
{ "index": { "_id": 3 }}
{ "productID" : "JODL-X-1937-#pV7","desc":"MBP" }


GET /products

POST /products/_search
{
  "query": {
    "term": {
      "desc": {
        "value": "iPhone"
        // "value":"iphone"
      }
    }
  }
}

POST /products/_search
{
  "query": {
    "term": {
      "desc.keyword": {
        "value": "iPhone"
        // "value":"iphone"
      }
    }
  }
}
```

几个思考题

- 几个查询的结果分别是什么
- 如果搜索不到，为什么
- 应该如何解决

Demo

- Term 查询的结果

多字段 Mapping 和 Term 查询



Code



```
POST /products/_search
{
  "query": {
    "term": {
      "productID": "XHDK-A-1293-#fJ3"
    }
  }
}

POST /products/_search
{
  "explain": true,
  "query": {
    "term": {
      "productID.keyword":"XHDK-A-1293-#fJ3"
    }
  }
}
```



![image-20220208104806406](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220208104806406.png)



复合查询 - Constant Score 转为 Filter

- 将 Query 转成 Filter，忽略 TF-IDF 计算，避免相关性算分的开销
- Filter 可以有效利用缓存



Code



```shell
POST /products/_search
{
  "explain": true,
  "query": {
    "constant_score": {
      "filter": {
        "term": {
          "productID.keyword":"XHDK-A-1293-#fJ3"
        }
      }
    }
  }
}
```

##### 基于全文的查询

基于全文本的查找：Match Query / Match Phrase Query / Query String Query

基于全文本的特点

- 索引和搜索时都会进行分词，查询字符串先传递到一个合适的分词器，然后生成一个供查询的词项列表
- 查询时候，先会对输入的查询进行分词，然后每个词项逐个进行底层的查询，最终将结果进行合并。并为每个文档生成一个算分。- 例如查 “Matrix reloaded”，会查到包括 Matrix 或者 reload的所有结果。

> Match Query Result

![image-20220208104859554](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220208104859554.png)

![image-20220208104928986](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220208104928986.png)

> Operator

![image-20220208105014131](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220208105014131.png)

![image-20220208105148327](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220208105148327.png)

![image-20220208105225001](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220208105225001.png)

> Match Phrase Query

![image-20220208105315719](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220208105315719.png)



![image-20220208105336833](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220208105336833.png)



> Match Query 查询过程

![image-20220208105401744](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220208105401744.png)



本节知识点回顾

- 基于词项的查找 vs 基于全文的查找
- 通过字段 Mapping 控制字段的分词；“Text” vs “Keyword”
- 通过参数控制查询的 Precision & Recall
- 复合查询 – Constant Score 查询
  - 即便是对 Keyword 进行 Term 查询，同样会进行算分
  - 可以将查询转为 Filtering，取消相关性算分的环节，以提升性能



Code



```
#设置 position_increment_gap
DELETE groups
PUT groups
{
  "mappings": {
    "properties": {
      "names":{
        "type": "text",
        "position_increment_gap": 0
      }
    }
  }
}

GET groups/_mapping

POST groups/_doc
{  "names": [ "John Water", "Water Smith"]  }

POST groups/_search
{
  "query": {
    "match_phrase": {
      "names": {
        "query": "Water Water",
        "slop": 100
      }
    }
  }
}

POST groups/_search
{
  "query": {
    "match_phrase": {
      "names": "Water Smith"
    }
  }
}
```

相关阅读

- https://www.elastic.co/guide/en/elasticsearch/reference/7.1/term-level-queries.html
- https://www.elastic.co/guide/en/elasticsearch/reference/7.1/full-text-queries.html

#### 25 | 结构化搜索

结构化数据

结构化搜索（Structured search） 是指对结构化数据的搜索；日期，布尔类型和数字都是结构化的

文本也可以是结构化的

- 如彩色笔可以有离散的颜色集合： 红（red） 、 绿（green） 、 蓝（blue）
- 一个博客可能被标记了标签，例如，分布式（distributed） 和 搜索（search）
- 电商网站上的商品都有 UPCs（通用产品码 Universal Product Codes）或其他的唯一标识，它们都需要遵从严格规定的、结构化的格式。

ES 中的结构化搜索

- 布尔，时间，日期和数字这类结构化数据：有精确的格式，我们可以对这些格式进行逻辑操作。包括比较数字或时间的范围，或判定两个值的大小。
- 结构化的文本可以做精确匹配或者部分匹配；例如 Term 查询 / Prefix 前缀查询
- 结构化结果只有“是”或“否”两个值；根据场景需要，可以决定结构化搜索是否需要打分

布尔值



Code



```shell
#结构化搜索，精确匹配
DELETE products
POST /products/_bulk
{ "index": { "_id": 1 }}
{ "price" : 10,"avaliable":true,"date":"2018-01-01", "productID" : "XHDK-A-1293-#fJ3" }
{ "index": { "_id": 2 }}
{ "price" : 20,"avaliable":true,"date":"2019-01-01", "productID" : "KDKE-B-9947-#kL5" }
{ "index": { "_id": 3 }}
{ "price" : 30,"avaliable":true, "productID" : "JODL-X-1937-#pV7" }
{ "index": { "_id": 4 }}
{ "price" : 30,"avaliable":false, "productID" : "QQPX-R-3956-#aD8" }

GET products/_mapping

#对布尔值 match 查询，有算分
POST products/_search
{
  "profile": "true",
  "explain": true,
  "query": {
    "term": {
      "avaliable": true
    }
  }
}

#对布尔值，通过constant score 转成 filtering，没有算分
POST products/_search
{
  "profile": "true",
  "explain": true,
  "query": {
    "constant_score": {
      "filter": {
        "term": {
          "avaliable": true
        }
      }
    }
  }
}
```

数字 Range

- gt 大于
- lt 小于
- gte 大于等于
- lte 小于等于



Code



```
#数字类型 Term
POST products/_search
{
  "profile": "true",
  "explain": true,
  "query": {
    "term": {
      "price": 30
    }
  }
}

#数字类型 terms
POST products/_search
{
  "query": {
    "constant_score": {
      "filter": {
        "terms": {
          "price": [
            "20",
            "30"
          ]
        }
      }
    }
  }
}

#数字 Range 查询
GET products/_search
{
    "query" : {
        "constant_score" : {
            "filter" : {
                "range" : {
                    "price" : {
                        "gte" : 20,
                        "lte"  : 30
                    }
                }
            }
        }
    }
}
```

日期 Range

- y : 年
- M : 月
- w : 周
- d : 天
- H / h : 小时
- m : 分钟
- s : 秒



Code



```shell
# 日期 range
POST products/_search
{
    "query" : {
        "constant_score" : {
            "filter" : {
                "range" : {
                    "date" : {
                      "gte" : "now-1y"
                    }
                }
            }
        }
    }
}

#exists查询
POST products/_search
{
  "query": {
    "constant_score": {
      "filter": {
        "exists": {
          "field": "date"
        }
      }
    }
  }
}
```

处理空值

查找多个精确值

包含而不是相等

解决方案：增加一个 genre_count 字段进行计数。会在组合 bool query 给出解决方法



Code



```
#处理多值字段
POST /movies/_bulk
{ "index": { "_id": 1 }}
{ "title" : "Father of the Bridge Part II","year":1995, "genre":"Comedy"}
{ "index": { "_id": 2 }}
{ "title" : "Dave","year":1993,"genre":["Comedy","Romance"] }

#处理多值字段，term 查询是包含，而不是等于
POST movies/_search
{
  "query": {
    "constant_score": {
      "filter": {
        "term": {
          "genre.keyword": "Comedy"
        }
      }
    }
  }
}
```

本节知识点回顾

- 结构化数据 & 结构化搜索
  - 如果不需要算分，可以通过 Constant Score，将查询转为 Filtering
- 范围查询和 Date Math
- 使用 Exist 查询处理非空 Null 值
- 精确值 & 多值字段的精确值查找
  - Term 查询是包含，不是完全相等。针对多值字段查询要尤其注意

demo



Code



```
# term 和 match 的比较
POST products/_search
{
  "profile": "true",
  "explain": true,
  "query": {
    "term": {
      "date": "2019-01-01"
    }
  }
}

POST products/_search
{
  "profile": "true",
  "explain": true,
  "query": {
    "match": {
      "date": "2019-01-01"
    }
  }
}


#对布尔数值
POST products/_search
{
  "query": {
    "constant_score": {
      "filter": {
        "term": {
          "avaliable": "false"
        }
      }
    }
  }
}

POST products/_search
{
  "query": {
    "term": {
      "avaliable": {
        "value": "false"
      }
    }
  }
}

POST products/_search
{
  "query": {
    "constant_score": {
      "filter": {
        "bool": {
          "must_not": {
            "exists": {
              "field": "date"
            }
          }
        }
      }
    }
  }
}
```

#### 26 | 搜索的相关性算分

##### 相关性和相关性算分

相关性 – Relevance

搜索的相关性算分，描述了一个**文档**和**查询语句**匹配的程度。ES 会对每个匹配查询条件的结果进行算分 _score

打分的本质是排序，需要把最符合用户需求的文档排在前面。ES 5 之前，默认的相关性算分采用 TF-IDF，现在采用 BM 25

词频 TF

- Term Frequency：检索词在一篇文档中出现的频率
  - 检索词出现的次数除以文档的总字数
- 度量一条查询和结果文档相关性的简单方法：简单将搜索中每一个 词的 TF 进行相加
  - TF(区块链) + TF(的) + TF(应用)
- Stop Word
  - “的” 在文档中出现了很多次，但是对贡献相关度几乎没有用处，不应该考虑他们的 TF

逆文档频率 IDF

- DF：检索词在所有文档中出现的频率
  - “区块链”在相对比较少的文档中出现
  - “应用”在相对比较多的文档中出现
  - “Stop Word” 在大量的文档中出现
- Inverse Document Frequency ：简单说 = log(全部文档数/检索词出现过的文档总数）
- TF-IDF 本质上就是将 TF 求和变成了加权求和
  - TF(区块链)*IDF(区块链) + TF(的)*IDF(的)+ TF(应用)*IDF(应用)

|        | 出现的文档数 | 总文档数 | IDF             |
| ------ | ------------ | -------- | --------------- |
| 区块链 | 200 万       | 10 亿    | log(500) = 8.96 |
| 的     | 10 亿        | 10 亿    | log(1) = 0      |
| 应用   | 5 亿         | 10 亿    | Log(2) = 1      |

TF-IDF 的概念

TF-IDF 被公认为是信息检索领域最重要的发明

除了在信息检索，在文献分类和其他相关领域有着非常广泛的应用

IDF 的概念，最早是剑桥大学的“斯巴克.琼斯”提出

- 1972年 – “关键词特殊性的统计解释和它在文献检索中的应用”
- 但是没有从理论上解释 IDF 应该是用 log(全部文档数/检索词出现过的文档总数），而不是其他函数。也没有做进一步的研究
- 1970，1980年代萨尔顿和罗宾逊，进行了进一步的证明和研究，并用香农信息论做了证明
  - http://www.staff.city.ac.uk/~sb317/papers/foundations_bm25_review.pdf
- 现代搜索引擎，对 TF-IDF 进行了大量细微的优化

Lucene中的 TF-IDF 评分公式

<img src="/Users/kevinlee/Library/Application Support/typora-user-images/image-20220210102349288.png" alt="image-20220210102349288" style="zoom:80%;" />

TF-IDF 评分公式

BM 25

- 从 ES 5 开始，默认算法改为 BM 25
- 和经典的TF-IDF相比，当 TF 无限增加时， BM 25算分会趋于一个数值

![image-20220210102620461](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220210102620461.png)

定制 Similarity

![image-20220210102712496](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220210102712496.png)

- K 默认值是 1.2，数值越小，饱和度越高，b 默认值是0.75（取值范围 0-1），0 代表禁止 Normalization

通过 Explain API 查看 TF-IDF

- 4 篇文档 + 4 个 Term 查询
- 思考一下
  - 查询中的 TF 和 IDF ？
  - 结果如何排序？
  - 文档⻓短 /TF / IDF 对相关度算分的影响



Code



```apl
PUT testscore
{
  "settings": {
    "number_of_shards": 1
  },
  "mappings": {
    "properties": {
      "content": {
        "type": "text"
      }
    }
  }
}

PUT testscore/_bulk
{ "index": { "_id": 1 }}
{ "content":"we use Elasticsearch to power the search" }
{ "index": { "_id": 2 }}
{ "content":"we like elasticsearch" }
{ "index": { "_id": 3 }}
{ "content":"The scoring of documents is caculated by the scoring formula" }
{ "index": { "_id": 4 }}
{ "content":"you know, for search" }


POST /testscore/_search
{
  //"explain": true,
  "query": {
    "match": {
      "content":"you"
      //"content": "elasticsearch"
      //"content":"the"
      //"content": "the elasticsearch"
    }
  }
}
```

Boosting Relevance

Boosting 是控制相关度的一种手段；例如 索引，字段 或查询子条件

参数 boost的含义

- 当 boost > 1 时，打分的相关度相对性提升
- 当 0 < boost < 1 时，打分的权重相对性降低
- 当 boost < 0 时，贡献负分



Code



```
POST testscore/_search
{
    "query": {
        "boosting" : {
            "positive" : {
                "term" : {
                    "content" : "elasticsearch"
                }
            },
            "negative" : {
                 "term" : {
                     "content" : "like"
                }
            },
            "negative_boost" : 0.2
        }
    }
}
```

本节知识点回顾

- 什么是相关性 & 相关性算分介绍
  - TF-IDF / BM25
- 在 Elasticsearch 中定制相关度算法的参数
- ES 中可以对索引，字段分别设置 Boosting 参数

#### 27 | Query & Filtering 与多字符串多字段查询

Query Context vs. Filter Context

- 高级搜索的功能：支持多项文本输入，针对多个字段进行搜索。
- 搜索引擎一般也提供基于时间，价格等条件的过滤
- 在 Elasticsearch 中，有Query 和 Filter 两种不同的 Context
  - Query Context：相关性算分
  - Filter Context：不需要算分（ Yes or No）， 可以利用 Cache， 获得更好的性能

条件组合

假设要搜索一本电影，包含了以下一些条件：评论中包含了 Guitar，用户打分高于 3 分，同时上映日期要在 1993 与 2000 年之间

这个搜索其实包含了 3 段逻辑，针对不同的字段；评论字段中要包含 Guitar / 用户评分大于 3 / 上映日期日期需要在给定的范围

同时包含这三个逻辑，并且有比较好的性能？复合查询： bool Query

bool查询

一个 bool 查询，是一个或者多个查询子句的组合；比如 总共包括 4 种子句。其中 2 种会影响算分，2 种不影响算分

相关性并不只是全文本检索的专利。也适用于 yes | no 的子句，匹配的子句越多，相关性评分越高。如果多条查询子句被合并为一条复合查询语句 ，比如 bool 查询，则每个查询子句计算得出的评分会被合并到总的相关性评分中。

- must：必须匹配。 贡献算分
- should: 选择性匹配。贡献算分
- must_not: **Filter Context** 查询字句，必须不能匹配
- filter: **Filter Context** 必须匹配，但是不贡献算分

bool 查询语法

- 子查询可以任意顺序出现
- 可以嵌套多个查询
- 如果你的 bool 查询中，没有 must 条件， should 中必须至少满足一条查询



Code



```
DELETE /products
POST /products/_bulk
{ "index": { "_id": 1 }}
{ "price" : 10,"avaliable":true,"date":"2018-01-01", "productID" : "XHDK-A-1293-#fJ3" }
{ "index": { "_id": 2 }}
{ "price" : 20,"avaliable":true,"date":"2019-01-01", "productID" : "KDKE-B-9947-#kL5" }
{ "index": { "_id": 3 }}
{ "price" : 30,"avaliable":true, "productID" : "JODL-X-1937-#pV7" }
{ "index": { "_id": 4 }}
{ "price" : 30,"avaliable":false, "productID" : "QQPX-R-3956-#aD8" }

#基本语法
POST /products/_search
{
  "query": {
    "bool" : {
      "must" : {
        "term" : { "price" : "30" }
      },
      "filter": {
        "term" : { "avaliable" : "true" }
      },
      "must_not" : {
        "range" : {
          "price" : { "lte" : 10 }
        }
      },
      "should" : [
        { "term" : { "productID.keyword" : "JODL-X-1937-#pV7" } },
        { "term" : { "productID.keyword" : "XHDK-A-1293-#fJ3" } }
      ],
      "minimum_should_match" :1
    }
  }
}
```

如何解决结构化查询 - “包含而不是相等”的问题

解决方案：增加一个 genre count 字段进行计数, 使用 bool 查询解决

从业务⻆度，按需改进 Elasticsearch 数据模型



Code



```
#改变数据模型，增加字段。解决数组包含而不是精确匹配的问题
POST /newmovies/_bulk
{ "index": { "_id": 1 }}
{ "title" : "Father of the Bridge Part II","year":1995, "genre":"Comedy","genre_count":1 }
{ "index": { "_id": 2 }}
{ "title" : "Dave","year":1993,"genre":["Comedy","Romance"],"genre_count":2 }

#must，有算分
POST /newmovies/_search
{
  "query": {
    "bool": {
      "must": [
        {"term": {"genre.keyword": {"value": "Comedy"}}},
        {"term": {"genre_count": {"value": 1}}}

      ]
    }
  }
}

#Filter。不参与算分，结果的score是0
POST /newmovies/_search
{
  "query": {
    "bool": {
      "filter": [
        {"term": {"genre.keyword": {"value": "Comedy"}}},
        {"term": {"genre_count": {"value": 1}}}
        ]
    }
  }
}
```

Filter Context - 不影响算分



Code



```
#Filtering Context
POST _search
{
  "query": {
    "bool" : {
      "filter": {
        "term" : { "avaliable" : "true" }
      },
      "must_not" : {
        "range" : {
          "price" : { "lte" : 10 }
        }
      }
    }
  }
}
```

Query Context - 影响算分



Code



```
#Query Context
POST /products/_bulk
{ "index": { "_id": 1 }}
{ "price" : 10,"avaliable":true,"date":"2018-01-01", "productID" : "XHDK-A-1293-#fJ3" }
{ "index": { "_id": 2 }}
{ "price" : 20,"avaliable":true,"date":"2019-01-01", "productID" : "KDKE-B-9947-#kL5" }
{ "index": { "_id": 3 }}
{ "price" : 30,"avaliable":true, "productID" : "JODL-X-1937-#pV7" }
{ "index": { "_id": 4 }}
{ "price" : 30,"avaliable":false, "productID" : "QQPX-R-3956-#aD8" }


POST /products/_search
{
  "query": {
    "bool": {
      "should": [
        {
          "term": {
            "productID.keyword": {
              "value": "JODL-X-1937-#pV7"}}
        },
        {"term": {"avaliable": {"value": true}}
        }
      ]
    }
  }
}
```

bool 嵌套

实现了 should not 的逻辑



Code



```
#嵌套，实现了 should not 逻辑
POST /products/_search
{
  "query": {
    "bool": {
      "must": {
        "term": {
          "price": "30"
        }
      },
      "should": [
        {
          "bool": {
            "must_not": {
              "term": {
                "avaliable": "false"
              }
            }
          }
        }
      ],
      "minimum_should_match": 1
    }
  }
}
#Controll the Precision
POST _search
{
  "query": {
    "bool" : {
      "must" : {
        "term" : { "price" : "30" }
      },
      "filter": {
        "term" : { "avaliable" : "true" }
      },
      "must_not" : {
        "range" : {
          "price" : { "lte" : 10 }
        }
      },
      "should" : [
        { "term" : { "productID.keyword" : "JODL-X-1937-#pV7" } },
        { "term" : { "productID.keyword" : "XHDK-A-1293-#fJ3" } }
      ],
      "minimum_should_match" :2
    }
  }
}
```

查询语句的结构，会对相关度算分产生影响

同一层级下的竞争字段，具有有相同的权重

通过嵌套 bool 查询，可以改变对算分的影响



Code



```
POST /animals/_search
{
  "query": {
    "bool": {
      "should": [
        { "term": { "text": "brown" }},
        { "term": { "text": "red" }},
        { "term": { "text": "quick"   }},
        { "term": { "text": "dog"   }}
      ]
    }
  }
}

POST /animals/_search
{
  "query": {
    "bool": {
      "should": [
        { "term": { "text": "quick" }},
        { "term": { "text": "dog"   }},
        {
          "bool":{
            "should":[
               { "term": { "text": "brown" }},
                 { "term": { "text": "brown" }},
            ]
          }

        }
      ]
    }
  }
}
```

控制字段的 Boosting



Code



```
DELETE blogs
POST /blogs/_bulk
{ "index": { "_id": 1 }}
{"title":"Apple iPad", "content":"Apple iPad,Apple iPad" }
{ "index": { "_id": 2 }}
{"title":"Apple iPad,Apple iPad", "content":"Apple iPad" }


POST blogs/_search
{
  "query": {
    "bool": {
      "should": [
        {"match": {
          "title": {
            "query": "apple,ipad",
            "boost": 4
          }
        }},

        {"match": {
          "content": {
            "query": "apple,ipad",
            "boost": 1
          }
        }}
      ]
    }
  }
}
```

Not Quit Not

要求苹果公司的产品信息优先



Code



```
DELETE news
POST /news/_bulk
{ "index": { "_id": 1 }}
{ "content":"Apple Mac" }
{ "index": { "_id": 2 }}
{ "content":"Apple iPad" }
{ "index": { "_id": 3 }}
{ "content":"Apple employee like Apple Pie and Apple Juice" }


POST news/_search
{
  "query": {
    "bool": {
      "must": {
        "match":{"content":"apple"}
      }
    }
  }
}
```

==Boosting Query-用于不排除搜索结果，将匹配低的放到最后==



Code



```
POST news/_search
{
  "query": {
    "bool": {
      "must": {
        "match":{"content":"apple"}
      },
      "must_not": {
        "match":{"content":"pie"}
      }
    }
  }
}

POST news/_search
{
  "query": {
    "boosting": {
      "positive": {
        "match": {
          "content": "apple"
        }
      },
      "negative": {
        "match": {
          "content": "pie"
        }
      },
      "negative_boost": 0.5
    }
  }
}
```

本节知识点回顾

- Query Context vs. Filter Context
- Bool Query – 更多的条件组合
- 查询结构与相关性算分
- 如何控制查询的精确度：Boosting & Boosting Query

相关阅读

- https://www.elastic.co/guide/en/elasticsearch/reference/current/query-filter-context.html
- https://www.elastic.co/guide/en/elasticsearch/reference/7.1/query-dsl-boosting-query.html

#### 28 | 单字符串多字段查询：Dis Max Query

单字符串查询

- Google 只提供一个输入框，查询相关的多个字段
- 支持按照价格，时间等进行过滤

单字符串查询的实例

- 博客标题: 文档 1 中出现 “Brown”
- 博客内容:
  - 文档 1 中出现了 “Brown”;
  - “Brown fox” 在文档 2 中全部出现，并且保持和查询一致的顺序（目测相关性最高）



Code



```
PUT /blogs/_doc/1
{
    "title": "Quick brown rabbits",
    "body":  "Brown rabbits are commonly seen."
}

PUT /blogs/_doc/2
{
    "title": "Keeping pets healthy",
    "body":  "My quick brown fox eats rabbits on a regular basis."
}

POST /blogs/_search
{
    "query": {
        "bool": {
            "should": [
                { "match": { "title": "Brown fox" }},
                { "match": { "body":  "Brown fox" }}
            ]
        }
    }
}
```

算分过程

- 查询 should 语句中的两个查询
- 加和两个查询的评分
- 乘以匹配语句的总数
- 除以所有语句的总数

查询结果及分析

Disjunction Max Query 查询

上例中，title 和 body 相互竞争

- 不应该将分数简单叠加，而是应该找到单个最佳匹配的字段的评分

Disjunction Max Query

- 将任何与任一查询匹配的文档作为结果返回。采用字段上最匹配的评分最终评分返回



Code



```
POST blogs/_search
{
    "query": {
        "dis_max": {
            "queries": [
                { "match": { "title": "Quick pets" }},
                { "match": { "body":  "Quick pets" }}
            ]
        }
    }
}
```

最佳字段查询调优

有一些情况下，同时匹配 title 和 body 字段的文档比只与一个字段匹配的文档的相关度更高

但 disjunction max query 查询只会简单地使用单个最佳匹配语句的评分 _score 作为整体评分。怎么办？



Code



```
POST blogs/_search
{
    "query": {
        "dis_max": {
            "queries": [
                { "match": { "title": "Quick pets" }},
                { "match": { "body":  "Quick pets" }}
            ]
        }
    }
}
```

通过 Tie Breaker 参数调整

1. 获得最佳匹配语句的评分 _score 。
2. 将其他匹配语句的评分与 tie_breaker 相乘
3. 对以上评分求和并规范化

Tier Breaker 是一个介于 0-1 之间的浮点数。0 代表使用最佳匹配；1 代表所有语句同等重要。



Code



```
POST blogs/_search
{
    "query": {
        "dis_max": {
            "queries": [
                { "match": { "title": "Quick pets" }},
                { "match": { "body":  "Quick pets" }}
            ],
            "tie_breaker": 0.2
        }
    }
}
```

本节知识点回顾

- 使用 bool 查询实现单字符串多字段查询
- 单字符串多字段查询时，如何在多个字段上进行算分
- 复合查询：Disjunction Max Query
  - 将评分最高的字段评分作为结果返回，满足两个字段是竞争关系的场景
- 对最佳字段查询进行调优：通过控制 Tie Breaker 参数，引入其他字段对算分的一些影响



Code



```
PUT /blogs/_doc/1
{
    "title": "Quick brown rabbits",
    "body":  "Brown rabbits are commonly seen."
}

PUT /blogs/_doc/2
{
    "title": "Keeping pets healthy",
    "body":  "My quick brown fox eats rabbits on a regular basis."
}

POST /blogs/_search
{
    "query": {
        "bool": {
            "should": [
                { "match": { "title": "Brown fox" }},
                { "match": { "body":  "Brown fox" }}
            ]
        }
    }
}

POST blogs/_search
{
    "query": {
        "dis_max": {
            "queries": [
                { "match": { "title": "Quick pets" }},
                { "match": { "body":  "Quick pets" }}
            ]
        }
    }
}

POST blogs/_search
{
    "query": {
        "dis_max": {
            "queries": [
                { "match": { "title": "Quick pets" }},
                { "match": { "body":  "Quick pets" }}
            ],
            "tie_breaker": 0.2
        }
    }
}
```

相关阅读

- https://www.elastic.co/guide/en/elasticsearch/reference/7.1/query-dsl-dis-max-query.html

---

#### 29 | 单字符串多字段查询：Multi Match

三种场景

- 最佳字段 (Best Fields): 当字段之间相互竞争，又相互关联。例如 title 和 body 这样的字段。评分来自最匹配字段
- 多数字段 (Most Fields): 处理英文内容时：一种常⻅的手段是，在主字段( English Analyzer)，抽取词干，加入同义词，以匹配更多的文档。相同的文本，加入子字段(Standard Analyzer)，以提供更加精确的匹配。其他字段作为匹配文档提高相关度的信号。匹配字段越多则越好
- 混合字段 (Cross Field): 对于某些实体，例如人名，地址，图书信息。需要在多个字段中确定信息，单个字段只能作为整体的一部分。希望在任何这些列出的字段中找到尽可能多的词

Multi Match Query

Best Fields 是默认类型，可以不用指定

Minimum should match 等参数可以传递到生成的 query中



Code



```shell
POST blogs/_search
{
    "query": {
        "dis_max": {
            "queries": [
                { "match": { "title": "Quick pets" }},
                { "match": { "body":  "Quick pets" }}
            ],
            "tie_breaker": 0.2
        }
    }
}

POST blogs/_search
{
  "query": {
    "multi_match": {
      "type": "best_fields",
      "query": "Quick pets",
      "fields": ["title","body"],
      "tie_breaker": 0.2,
      "minimum_should_match": "20%"
    }
  }
}
```

一个查询案例

英文分词器，导致精确度降低，时态信息丢失



Code



```
PUT /titles
{
  "mappings": {
    "properties": {
      "title": {
        "type": "text",
        "analyzer": "english"
      }
    }
  }
}

POST titles/_bulk
{ "index": { "_id": 1 }}
{ "title": "My dog barks" }
{ "index": { "_id": 2 }}
{ "title": "I see a lot of barking dogds on the road " }


GET titles/_search
{
  "query": {
    "match": {
      "title": "barking dogs"
    }
  }
}
```

使用多数字段匹配解决

用广度匹配字段 title 包括尽可能多的文档——以提升召回率——同时又使用字段 title.std 作为信号 将相关度更高的文档置于结果顶部。

每个字段对于最终评分的贡献可以通过自定义值 boost 来控制。比如，使 title 字段更为重要， 这样同时也降低了其他信号字段的作用：



Code



```
DELETE /titles
PUT /titles
{
  "mappings": {
    "properties": {
      "title": {
        "type": "text",
        "analyzer": "english",
        "fields": {"std": {"type": "text","analyzer": "standard"}}
      }
    }
  }
}

POST titles/_bulk
{ "index": { "_id": 1 }}
{ "title": "My dog barks" }
{ "index": { "_id": 2 }}
{ "title": "I see a lot of barking dogs on the road " }

GET /titles/_search
{
   "query": {
        "multi_match": {
            "query":  "barking dogs",
            "type":   "most_fields",
            "fields": [ "title", "title.std" ]
        }
    }
}

GET /titles/_search
{
   "query": {
        "multi_match": {
            "query":  "barking dogs",
            "type":   "most_fields",
            "fields": [ "title^10", "title.std" ]
        }
    }
}
```

跨字段搜索

无法使用 Operator

可以用 copy_to 解决，但是需要额外的存储空间



Code



```
POST address/_msearch
{
  "query": {
    "multi_match": {
      "query": "Poland Street W1V",
      "type": "cross_fields",
      // "operator": "and",
      "fields": [ "street", "city", "country", "postcode" ]
    }
  }
}
```

支持使用 Operator

与 copy_to, 相比，其中一个优势就是它可以在搜索时为单个字段提升权重。



Code



```
POST address/_msearch
{
  "query": {
    "multi_match": {
      "query": "Poland Street W1V",
      "type": "cross_fields",
      "operator": "and",
      "fields": [ "street", "city", "country", "postcode" ]
    }
  }
}
```

本节知识点回顾

- Multi Match 查询的基本语法
- 查询的类型：最佳字段 / 多数字段 / 跨字段
- Boosting
- 控制 Precision：以及使用子字段多数字段算分，控制；使用 Operator

#### 30 | 多语言及中文分词与检索

自然语言与查询 Recall

当处理人类自然语言时，有些情况，尽管搜索和原文不完全匹配，但是希望搜到一些内容; 例如 Quick brown fox 和 fast brown fox / Jumping fox 和 Jumped foxes

一些可采取的优化

- 归一化词元：清除变音符号，如 rôle 的时候也会匹配 role
- 抽取词根：清除单复数和时态的差异
- 包含同义词
- 拼写错误：拼写错误，或者同音异形词

混合多语言的挑战

一些具体的多语言场景； 例如 不同的索引使用不同的语言 / 同一个索引中，不同的字段使用不同的语言 / 一个文档的一个字段内混合不同的语言

混合语言存在的一些挑战

- 词干提取：以色列文档，包含了希伯来语，阿拉伯语，俄语和英文
- 不正确的文档频率 – 英文为主的文章中，德文算分高（稀有）
- 需要判断用户搜索时使用的语言，语言识别（Compact Language Detector)，例如，根据语言，查询不同的索引

分词的挑战

英文分词：You’re 分成一个还是多个？Half-baked

中文分词：

- 分词标准：哈工大标准中，姓和名分开。 HanLP 是在一起的。具体情况需制定不同的标准
- 歧义 （组合型歧义，交集型歧义，真歧义），例如 中华人⺠共和国 / 美国会通过对台售武法案 / 上海仁和服装厂



Code



```
#stop word

DELETE my_index
PUT /my_index/_doc/1
{ "title": "I'm happy for this fox" }

PUT /my_index/_doc/2
{ "title": "I'm not happy about my fox problem" }

POST my_index/_search
{
  "query": {
    "match": {
      "title": "not happy fox"
    }
  }
}
```

中文分词方法的演变 - 字典法

查字典 - 最容易想到的分词方法 （北京航空大学的梁南元教授提出）

- 一个句子从左到右扫描一遍。遇到有的词就标示出来。找到复合词，就找最⻓的
- 不认识的字串就分割成单字词

最小词数的分词理论 – 哈工大王晓⻰博士把查字典的方法理论化

- 一句话应该分成数量最少的词串
- 遇到二义性的分割，无能为力（例如：“发展中国家” / “上海大学城书店”）
- 用各种文化规则来解决二义性，都并不成功

中文分词方法的演变 - 基于统计学的机器学习方法

统计语言模型 – 1990年前后，清华大学电子工程系郭进博士

解决了二义性问题，将中文分词的错误率降低了一个数量级。概率问题，动态规划 + 利用维特比算法快速找到最佳分词

基于统计的机器学习算法

这类目前常用的是算法是HMM、CRF、SVM、深度学习等算法。比如 Hanlp 分词工具是基于CRF 算法以CRF为例，基本思路是对汉字进行标注训练，不仅考虑了词语出现的频率，还考虑上下文， 具备较好的学习能力，因此其对歧义词和未登录词的识别都具有良好的效果。

随着深度学习的兴起，也出现了基于神经网络的分词器，有人尝试使用双向LSTM+CRF实现分词器， 其本质上是序列标注，据报道其分词器字符准确率可高达97.5%

中文分词现状

中文分词器以统计语言模型为基础，经过几十年的发展，今天基本已经可以看作是一个已经解决的问题

不同分词器的好坏，主要的差别在于数据的使用和工程使用的精度

常⻅的分词器都是使用机器学习算法和词典相结合，一方面能够提高分词准确率，另一方面能够改善领域适应性。

一些中文分词期

HanLP – 面向生产环境的自然语言处理工具包

- http://hanlp.com/
- https://github.com/KennFalcon/elasticsearch-analysis-hanlp

IK 分词器：https://github.com/medcl/elasticsearch-analysis-ik

HanLP Analysis

HanLP：./elasticsearch-plugin install https://github.com/KennFalcon/elasticsearch-analysis-hanlp/releases/download/v7.1.0/elasticsearch-analysis-hanlp-7.1.0.zip

也可以通过远程字典配置

IK Analysis

HanLP：./elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.1.0/elasticsearch-analysis-ik-7.1.0.zip

特性：支持字典热更新

Pinyin Analysis

Pinyin：./elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-pinyin/releases/download/v7.1.0/elasticsearch-analysis-pinyin-7.1.0.zip

##### 中文分词Demo

使用不同的分词器测试效果

索引时，尽量切分的短，查询的时候，尽量用⻓的词

拼音分词器

- 来到杨过曾经生活过的地方，小龙女动情地说：“我也想过过过儿过过的生活。”
- 你也想犯范范玮琪犯过的错吗
- 校长说衣服上除了校徽别别别的
- 这几天天天天气不好
- 我背有点驼，麻麻说“你的背得背背背背佳

分词期有：analysis-icu，



Code



```
#ik_max_word
#ik_smart
#hanlp: hanlp默认分词
#hanlp_standard: 标准分词
#hanlp_index: 索引分词
#hanlp_nlp: NLP分词
#hanlp_n_short: N-最短路分词
#hanlp_dijkstra: 最短路分词
#hanlp_crf: CRF分词（在hanlp 1.6.6已开始废弃）
#hanlp_speed: 极速词典分词

POST _analyze
{
  "analyzer": "hanlp_standard",
  "text": ["剑桥分析公司多位高管对卧底记者说，他们确保了唐纳德·特朗普在总统大选中获胜"]
}

#Pinyin
PUT /artists/
{
    "settings" : {
        "analysis" : {
            "analyzer" : {
                "user_name_analyzer" : {
                    "tokenizer" : "whitespace",
                    "filter" : "pinyin_first_letter_and_full_pinyin_filter"
                }
            },
            "filter" : {
                "pinyin_first_letter_and_full_pinyin_filter" : {
                    "type" : "pinyin",
                    "keep_first_letter" : true,
                    "keep_full_pinyin" : false,
                    "keep_none_chinese" : true,
                    "keep_original" : false,
                    "limit_first_letter_length" : 16,
                    "lowercase" : true,
                    "trim_whitespace" : true,
                    "keep_none_chinese_in_first_letter" : true
                }
            }
        }
    }
}

GET /artists/_analyze
{
  "text": ["刘德华 张学友 郭富城 黎明 四大天王"],
  "analyzer": "user_name_analyzer"
}
```

##### 本节知识点回顾

多语言搜索的挑战，例如 分词 / 语言检测 / 相关性算分

Elasticsearch 中，多语言搜索所使用的一些技巧，例如 归一化词元 / 单词词根抽取 / 同义词 / 拼写错误

中文分词的演进及一些 ES 中文分词器 & 拼音分词器介绍

#### 31 | Space Jam，一次全文搜索的实例

目的

目标：用过一个具体案例，帮助你了解并巩固所学的知识点

- 写入数据 / 设置 Mapping，设置 Analysis
- 查询并高亮显示结果
- 分析查询结果，通过修改配置和查询，优化搜索的相关性

分析问题，结合原理，分析思考并加以实践

TMDB 数据库

创建于 2008 年，电影的 Meta Data 库, 有46 万本电影 / 12万本电视剧 / 230万张图片 / 每周 20万次编辑；他们 提供 API。总共有超过20万开发人员和公司在使用

数据导入

- 数据特征 – 标题信息较短 / 概述相对较⻓
- 通 过 TDMB Search API
- 将查询数据保存在本地 CSV 文件中
- 使用 Python 导入及查询数据
- 索引的主分片数设置为 1，使用默认 Dynamic Mapping

Use Case - 查找 Space Jam

空中大灌篮 (Space JAM)，有华纳公司动画明星 / 篮球巨星乔丹 / 外星小怪物

案例：用户不记得电影名，而希望通过一些关键字，搜索到电影的详细信息，搜索关键字：“Basketball with Cartoon Aliens”

思考和分析

- “精确值” 还是 “全文”？
- 搜索是怎么样的？不同的字段需要配置怎么样的分词器
- 测试不同的的选项
  - 分词期 / 多字段属性 / 是否要 g-grams / what are some critical synonyms / 为字段设置不同的权重
  - 测试不同的选项，测试不同的搜索条件

demo

- 查看 csv 文件中的电影源数据
- 使用默认 Dynamic Mapping，用 Python 导入数据
- 查看搜索结果和相关性算分 / 对搜索结果高亮显示
- 使用英文分词器 / 为查询语句设置不同的 Boosting / 增加子多字段

测试相关性 - 理解原理 + 多分析 + 多调整测试

技术分为道和术两种

- 道 – 原理和原则
- 术 – 具体的做法，具体的解法

关于搜索，为了有一个好的搜索结果。除了真正理解背后的原理，更需要多加实践与分析

- 单纯追求“术”，会一直很辛苦。只有掌握了本质和精髓之“道”，做事才能游刃有余
- 要做好搜索，除了理解原理，也需要坚持去分析一些不好的搜索结果。只有通过一定时间的积累， 才能真正有所感觉
- 总希望一个模型，一个算法，就能毕其功于一役，是不现实的

监控并且理解用户行为

- 不要过度调试相关度
- 而要监控搜索结果，监控用户点击最顶端结果的频次
- 将搜索结果提高到极高水平，唯一途径就是
  - 需要具有度量用户行为的强大能力
  - 可以在后台实现统计数据，比如，用户的查询和结果，有多少被点击了
  - 哪些搜索，没有返回结果

本节知识点回顾

- 目标：用过一个具体案例，帮助你了解并巩固所学的知识点
- 使用 Python 脚本导入及查询数据 / Mapping 设定
- Mapping 设定和分词器的选择至关重要
- 监控并理解用户行为 / 查询并调试相关度
  - Boosting 查询字段 / Explain API / 高亮显示

环境要求

- Python 2.7.15
- 可以使用pyenv管理多个python版本（可选）

进入 tmdb-search目录



Code



```
Run
pip install -r requirements.txt
Run python ./ingest_tmdb_from_file.py
```

通过不同的索引，查看搜索结果



Code



```
tmdb-search]# python ./ingest_tmdb_from_file.py
tmdb-search]# python query_tmdb.py
```

课程demo



Code



```
POST tmdb/_search
{
"_source": ["title","overview"],
 "query": {
   "match_all": {}
 }
}

POST tmdb/_search
{
  "_source": ["title","overview"],
  "query": {
    "multi_match": {
      "query": "basketball with cartoon aliens",
      "fields": ["title","overview"]
    }
  },
  "highlight" : {
        "fields" : {
            "overview" : { "pre_tags" : ["\\033[0;32;40m"], "post_tags" : ["\\033[0m"] },
            "title" : { "pre_tags" : ["\\033[0;32;40m"], "post_tags" : ["\\033[0m"] }

        }
    }
}
```

相关

- Windows 安装 pyenv https://github.com/pyenv-win/pyenv-win
- Mac 安装pyenv https://segmentfault.com/a/1190000017403221
- Linux 安装 pyenv https://blog.csdn.net/GX_1_11_real/article/details/80237064
- Python.org https://www.python.org/

#### 32 | 使 用 Search Template 和 Index Alias

Search Template - 解偶程序 & 搜索 DSL

Elasticsearch 的查询语句, 对相关性算分 / 查询性能都至关重要

在开发初期，虽然可以明确查询参数，但是往往还不能最终定义查询的DSL的具体结构, 通过 Search Template 定义一个Contract; 通过 Search Template 定义一个Contract

各司其职，解耦, 例如 开发人员 / 搜索工程师 / 性能工程师

使用 Search Template 进行查询



Code



```
DELETE _scripts/tmdb
POST _scripts/tmdb
{
  "script": {
    "lang": "mustache",
    "source": {
      "_source": [
        "title","overview"
      ],
      "size": 20,
      "query": {
        "multi_match": {
          "query": "{{q}}",
          "fields": ["title","overview"]
        }
      }
    }
  }
}

GET _scripts/tmdb

POST tmdb/_search/template
{
    "id":"tmdb",
    "params": {
        "q": "basketball with cartoon aliens"
    }
}
```

Index Alias 实现零停机运维

- 为索引定义一个别名
- 通过别名读写数据



Code



```
PUT movies-2019/_doc/1
{
  "name":"the matrix",
  "rating":5
}

PUT movies-2019/_doc/2
{
  "name":"Speed",
  "rating":3
}

POST _aliases
{
  "actions": [
    {
      "add": {
        "index": "movies-2019",
        "alias": "movies-latest"
      }
    }
  ]
}

POST movies-latest/_search
{
  "query": {
    "match_all": {}
  }
}
```

使用 Alias 创建不同查询的视图



Code



```
POST _aliases
{
  "actions": [
    {
      "add": {
        "index": "movies-2019",
        "alias": "movies-lastest-highrate",
        "filter": {
          "range": {
            "rating": {
              "gte": 4
            }
          }
        }
      }
    }
  ]
}

POST movies-lastest-highrate/_search
{
  "query": {
    "match_all": {}
  }
}
```

本期知识点回顾

Search Template 的使用场景：如果通过 Mocha 语法定义一个 Search Template

Index Alias 的使用场景

1. 如何创建与使用 Index Alias
2. 通过 Index Alias 创建不同的查询视图