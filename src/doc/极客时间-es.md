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



```shell
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



```shell
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

#### 33 | 综合排序：Function Score Query 优化算分

算分和排序

- Elasticsearch 默认会以文档的相关度算分进行排序
- 可以通过指定一个或者多个字段进行排序
- 使用相关度算分(score)排序，不能满足某些特定条件
  - 无法针对相关度，对排序实现更多的控制

Function Score Query

可以在查询结束后，对每一个匹配的文档进行一系列的重新算分，根据新生成的分数进行排序。

提供了几种默认的计算分值的函数

- Weight ：为每一个文档设置一个简单而不被规范化的权重
- Field Value Factor：使用该数值来修改 _score，例如将 “热度”和“点赞数”作为算分的参考因素
- Random Score：为每一个用户使用一个不同的，随机算分结果
- 衰减函数： 以某个字段的值为标准，距离某个值越近，得分越高
- Script Score：自定义脚本完全控制所需逻辑

按受欢迎度提升权重

希望能够将点赞多的 blog，放在搜索列表相对靠前的位置。同时搜索的评分，还是要作为排序的主要依据

新的算分 = 老的算分 * 投票数；比如 投票数 为 0，或者 投票数很大时



Code



```sh
DELETE blogs
PUT /blogs/_doc/1
{
  "title":   "About popularity",
  "content": "In this post we will talk about...",
  "votes":   0
}

PUT /blogs/_doc/2
{
  "title":   "About popularity",
  "content": "In this post we will talk about...",
  "votes":   100
}

PUT /blogs/_doc/3
{
  "title":   "About popularity",
  "content": "In this post we will talk about...",
  "votes":   1000000
}

POST /blogs/_search
{
  "query": {
    "function_score": {
      "query": {
        "multi_match": {
          "query":    "popularity",
          "fields": [ "title", "content" ]
        }
      },
      "field_value_factor": {
        "field": "votes"
      }
    }
  }
}
```

使用 Modifier 平滑曲线

新的算分 = 老的算分 * log( 1 + 投票数 )



Code



```sh
POST /blogs/_search
{
  "query": {
    "function_score": {
      "query": {
        "multi_match": {
          "query":    "popularity",
          "fields": [ "title", "content" ]
        }
      },
      "field_value_factor": {
        "field": "votes",
        "modifier": "log1p"
      }
    }
  }
}
```

引入 Factor

新的算分 = 老的算分 * log( 1 + factor *投票数 )



Code



```sh
POST /blogs/_search
{
  "query": {
    "function_score": {
      "query": {
        "multi_match": {
          "query":    "popularity",
          "fields": [ "title", "content" ]
        }
      },
      "field_value_factor": {
        "field": "votes",
        "modifier": "log1p" ,
        "factor": 0.1
      }
    }
  }
}
```

Boost Mode 和 Max Boost

Boost Mode

- Multiply：算分与函数值的乘积
- Sum：算分与函数的和
- Min / Max：算分与函数取 最小/ 最大值
- Replace：使用函数值取代算分

Max Boost 可以将算分控制在一个最大值



Code



```
POST /blogs/_search
{
  "query": {
    "function_score": {
      "query": {
        "multi_match": {
          "query":    "popularity",
          "fields": [ "title", "content" ]
        }
      },
      "field_value_factor": {
        "field": "votes",
        "modifier": "log1p" ,
        "factor": 0.1
      },
      "boost_mode": "sum",
      "max_boost": 3
    }
  }
}
```

一致性随机函数

使用场景：网站的广告需要提高展现率

具体需求：让每个用户能看到不同的随机排名，但是也希望同一个用户访问时，结果的相对顺序，保持一致 (Consistently Random)



Code



```
POST /blogs/_search
{
  "query": {
    "function_score": {
      "random_score": {
        "seed": 911119
      }
    }
  }
}
```

本节知识点回顾

- 复合查询：Function Score Query：提供了多种函数，自定义脚本，完全控制算分
- Field Value Factor：搜索的相关度，能够结合投票的数量进行重算。通过一些参数的设定，对算分进行控制
- 随机函数：用户提供 Seed，返回一个随机一致性的排序结果

相关阅读

- https://www.elastic.co/guide/en/elasticsearch/reference/7.1/query-dsl-function-score-query.html

#### 34 | Term & Phrase Suggester[自动补全，推荐]

什么是搜索建议

现代的搜索引擎，一般都会提供 Suggest as you type 的功能，帮助用户在输入搜索的过程中，进行自动补全或者纠错。通过协助用户输入更加精准的关键词，提高后续搜索阶段文档匹配的程度

在 google 上搜索，一开始会自动补全。当输入到 一定⻓度，如因为单词拼写错误无法补全， 就会开始提示相似的词或者句子

Elasticsearch Suggester API

搜索引擎中类似的功能，在 Elasticsearch 中是通过 Suggester API 实现的

原理：将输入的文本分解为 Token，然后在索引的字典里查找相似的 Term 并返回

根据不同的使用场景，Elasticsearch 设计了 4 种类别的 Suggesters

- Term & Phrase Suggester
- Complete & Context Suggester

Term Suggester

Suggester 就是一种特殊类型的搜索。”text” 里是调用时候提供的文本，通常来自于用户界面上用户输入的内容,

用户输入的 “lucen” 是一个错误的拼写, 会到 指定的字段 “body” 上搜索，当无法搜索到结果时 (missing)，返回建议的词



Code



```sh
DELETE articles

POST articles/_bulk
{ "index" : { } }
{ "body": "lucene is very cool"}
{ "index" : { } }
{ "body": "Elasticsearch builds on top of lucene"}
{ "index" : { } }
{ "body": "Elasticsearch rocks"}
{ "index" : { } }
{ "body": "elastic is the company behind ELK stack"}
{ "index" : { } }
{ "body": "Elk stack rocks"}
{ "index" : {} }
{  "body": "elasticsearch is rock solid"}


POST _analyze
{
  "analyzer": "standard",
  "text": ["Elk stack  rocks rock"]
}

POST /articles/_search
{
  "size": 1,
  "query": {
    "match": {
      "body": "lucen rock"
    }
  },
  "suggest": {
    "term-suggestion": {
      "text": "lucen rock",
      "term": {
        "suggest_mode": "missing",
        "field": "body"
      }
    }
  }
}
```

一些测试数据

默认使用 standard 分词器

- 大写转小写
- rocks 和 rock 是两个词



Code



```
DELETE articles
PUT articles
{
  "mappings": {
    "properties": {
      "title_completion":{
        "type": "completion"
      }
    }
  }
}

POST articles/_bulk
{ "index" : { } }
{ "title_completion": "lucene is very cool"}
{ "index" : { } }
{ "title_completion": "Elasticsearch builds on top of lucene"}
{ "index" : { } }
{ "title_completion": "Elasticsearch rocks"}
{ "index" : { } }
{ "title_completion": "elastic is the company behind ELK stack"}
{ "index" : { } }
{ "title_completion": "Elk stack rocks"}
{ "index" : {} }


POST articles/_search?pretty
{
  "size": 0,
  "suggest": {
    "article-suggester": {
      "prefix": "elk ",
      "completion": {
        "field": "title_completion"
      }
    }
  }
}
```

Term Suggester - Missing Mode

搜索 “lucen rock”：每个建议都包含了一个算分，相似性是通过 Levenshtein Edit Distance 的算法实现的。核心思想就是一个词改动多少字符就可以和另外一个词一致。 提供了很多可选参数来控制相似性的模糊程度。例如 “max_edits”

几种 Suggestion Mode

- Missing – 如索引中已经存在，就不提供建议
- Popular – 推荐出现频率更加高的词
- Always – 无论是否存在，都提供建议

Term Suggester - Popular Mode



Code



```
POST /articles/_search
{

  "suggest": {
    "term-suggestion": {
      "text": "lucen rock",
      "term": {
        "suggest_mode": "popular",
        "field": "body"
      }
    }
  }
}
```

Sorting by Frequency & Prefix Length

默认按照 score 排序，也可以按照“frequency

默认首字⺟不一致就不会匹配推荐，但是如果将 prefix_length 设置为 0，就会为 hock 建议 rock



Code



```
POST /articles/_search
{

  "suggest": {
    "term-suggestion": {
      "text": "lucen hocks",
      "term": {
        "suggest_mode": "always",
        "field": "body",
        "prefix_length":0,
        "sort": "frequency"
      }
    }
  }
}
```

Phrase Suggester

Phrase Suggester 在 Term Suggester 上增加了一些额外的逻辑

一些参数

- Suggest Mode ：missing, popular, always
- Max Errors：最多可以拼错的 Terms 数
- Confidence：限制返回结果数，默认为 1



Code



```
POST /articles/_search
{
  "suggest": {
    "my-suggestion": {
      "text": "lucne and elasticsear rock hello world ",
      "phrase": {
        "field": "body",
        "max_errors":2,
        "confidence":0,
        "direct_generator":[{
          "field":"body",
          "suggest_mode":"always"
        }],
        "highlight": {
          "pre_tag": "<em>",
          "post_tag": "</em>"
        }
      }
    }
  }
}
```

本节知识点回顾

Term Suggester 和 Phrase Suggester 分别有三种不同类型的 Suggestion Mode

- Missing / Popular / Always
- 通过使用 Suggestion Phrase 可以提高搜索的 Precision 和 Recall

#### 35 | 自动补全与基于上下文的提示

##### The Completion Suggester

Completion Suggester 提供了“自动完成” (Auto Complete) 的功能。用户每输入一个字符，就需要即时发送一个查询请求到后段查找匹配项

对性能要求比较苛刻。Elasticsearch 采用了不同的数据结构，并非通过倒排索引来完成。而是将 Analyze 的数据编码成 FST 和索引一起存放。FST 会被 ES 整个加载进内存，速度很快

FST 只能用于前缀查找

使用 Completion Suggester 的一些步骤

- 定义Mapping，使用 “completion” type
- 索引数据
- 运行 “suggest” 查询，得到搜索建议



Code



```sh
DELETE articles
PUT articles
{
  "mappings": {
    "properties": {
      "title_completion":{
        "type": "completion"
      }
    }
  }
}
```

索引数据



Code



```sh
POST articles/_bulk
{ "index" : { } }
{ "title_completion": "lucene is very cool"}
{ "index" : { } }
{ "title_completion": "Elasticsearch builds on top of lucene"}
{ "index" : { } }
{ "title_completion": "Elasticsearch rocks"}
{ "index" : { } }
{ "title_completion": "elastic is the company behind ELK stack"}
{ "index" : { } }
{ "title_completion": "Elk stack rocks"}
{ "index" : {} }
```

搜索数据



Code



```
POST articles/_search?pretty
{
  "size": 0,
  "suggest": {
    "article-suggester": {
      "prefix": "elk ",
      "completion": {
        "field": "title_completion"
      }
    }
  }
}
```

什么是 Context Suggester

- Completion Suggester 的扩展
- 可以在搜索中加入更多的上下文信息，例如，输入 “star”
  - 咖啡相关：建议 “Starbucks”
  - 电影相关：”star wars”

实现 Context Suggester

可以定义两种类型的 Context

- Category – 任意的字符串
- Geo – 地理位置信息

● 实现 Context Suggester 的具体步骤

- 定制一个 Mapping
- 索引数据，并且为每个文档加入 Context 信息
- 结合 Context 进行 Suggestion 查询

定义 Mapping

- 增加 Contexts
  - Type
  - name



Code



```
DELETE comments

PUT comments/_mapping
{
  "properties": {
    "comment_autocomplete":{
      "type": "completion",
      "contexts":[{
        "type":"category",
        "name":"comment_category"
      }]
    }
  }
}
```

索引数据

设置不同的 Category



Code



```
POST comments/_doc
{
  "comment":"I love the star war movies",
  "comment_autocomplete":{
    "input":["star wars"],
    "contexts":{
      "comment_category":"movies"
    }
  }
}

POST comments/_doc
{
  "comment":"Where can I find a Starbucks",
  "comment_autocomplete":{
    "input":["starbucks"],
    "contexts":{
      "comment_category":"coffee"
    }
  }
}
```

不同的上下文，自动提示



Code



```
POST comments/_search
{
  "suggest": {
    "MY_SUGGESTION": {
      "prefix": "sta",
      "completion":{
        "field":"comment_autocomplete",
        "contexts":{
          "comment_category":"movies"
          // "comment_category":"coffee"
        }
      }
    }
  }
}
```

精准度和召回率

- 精准度: Completion > Phrase > Term
- 召回率: Term > Phrase > Completion
- 性能: Completion > Phrase > Term

本节知识点回顾

- Completion Suggester，对性能要求比较苛刻。采用了不同的数据结构，并非通过倒排索引来完成。而是将 Analyze 的数据编码成 FST 和索引一起存放。FST 会被 ES 整个加载进内存，速度很快
- 需要设置特定的 Mapping
- Context Completion Suggester 支持结合不同的上下文，给出推荐

#### 36 | 跨集群搜索

水平扩展的痛点

单集群 – 当水平扩展时，节点数不能无限增加。当集群的 meta 信息（节点，索引，集群状态）过多，会导致更新压力变大，单个 Active Master 会成为性能瓶颈，导致整个集群无法正常工作

早期版本，通过 Tribe Node 可以实现多集群访问的需求，但是还存在一定的问题

- Tribe Node 会以 Client Node 的方式加入每个集群。 集群中 Master 节点的任务变更需要 Tribe Node 的回应才能继续
- Tribe Node 不保存 Cluster State 信息，一旦重启，初始化很慢
- 当多个集群存在索引重名的情况时，只能设置一种 Prefer 规则

跨集群搜索 - Cross Cluster Search

早期 Tribe Node 的方案存在一定的问题，现已被 Deprecated

Elasticsearch 5.3 引入了跨集群搜索的功能(Cross Cluster Search)，推荐使用

- 允许任何节点扮演 federated 节点，以轻量的方式，将搜索请求进行代理
- 不需要以 Client Node 的形式加入其他集群

Demo

- 配置跨集群搜索
- 每个集群创建相同的索引名，并写入数据
- 跨集群搜索
- 在 Kibana 的 Index Pattern 中配置跨集群搜索

配置及查询



Code



```sh
bin/elasticsearch -E node.name=cluster0node -E cluster.name=cluster0 -E path.data=cluster0_data -E discovery.type=single-node -E http.port=9200 -E transport.port=9300 -d
bin/elasticsearch -E node.name=cluster1node -E cluster.name=cluster1 -E path.data=cluster1_data -E discovery.type=single-node -E http.port=9201 -E transport.port=9301 -d
bin/elasticsearch -E node.name=cluster2node -E cluster.name=cluster2 -E path.data=cluster2_data -E discovery.type=single-node -E http.port=9202 -E transport.port=9302 -d
```

配置集群



Code



```sh
//在每个集群上设置动态的设置
PUT _cluster/settings
{
  "persistent": {
    "cluster": {
      "remote": {
        "cluster0": {
          "seeds": [
            "127.0.0.1:9300"
          ],
          "transport.ping_schedule": "30s"
        },
        "cluster1": {
          "seeds": [
            "127.0.0.1:9301"
          ],
          "transport.compress": true,
          "skip_unavailable": true
        },
        "cluster2": {
          "seeds": [
            "127.0.0.1:9302"
          ]
        }
      }
    }
  }
}

#cURL
curl -XPUT "http://localhost:9200/_cluster/settings" -H 'Content-Type: application/json' -d'
{"persistent":{"cluster":{"remote":{"cluster0":{"seeds":["127.0.0.1:9300"],"transport.ping_schedule":"30s"},"cluster1":{"seeds":["127.0.0.1:9301"],"transport.compress":true,"skip_unavailable":true},"cluster2":{"seeds":["127.0.0.1:9302"]}}}}}'

curl -XPUT "http://localhost:9201/_cluster/settings" -H 'Content-Type: application/json' -d'
{"persistent":{"cluster":{"remote":{"cluster0":{"seeds":["127.0.0.1:9300"],"transport.ping_schedule":"30s"},"cluster1":{"seeds":["127.0.0.1:9301"],"transport.compress":true,"skip_unavailable":true},"cluster2":{"seeds":["127.0.0.1:9302"]}}}}}'

curl -XPUT "http://localhost:9202/_cluster/settings" -H 'Content-Type: application/json' -d'
{"persistent":{"cluster":{"remote":{"cluster0":{"seeds":["127.0.0.1:9300"],"transport.ping_schedule":"30s"},"cluster1":{"seeds":["127.0.0.1:9301"],"transport.compress":true,"skip_unavailable":true},"cluster2":{"seeds":["127.0.0.1:9302"]}}}}}'
```

创建测试数据并查询



Code



```shell
#创建测试数据
curl -XPOST "http://localhost:9200/users/_doc" -H 'Content-Type: application/json' -d'
{"name":"user1","age":10}'

curl -XPOST "http://localhost:9201/users/_doc" -H 'Content-Type: application/json' -d'
{"name":"user2","age":20}'

curl -XPOST "http://localhost:9202/users/_doc" -H 'Content-Type: application/json' -d'
{"name":"user3","age":30}'

#查询
GET /users,cluster1:users,cluster2:users/_search
{
  "query": {
    "range": {
      "age": {
        "gte": 20,
        "lte": 40
      }
    }
  }
}
```

本节知识点回顾

- 当集群无法水平扩展，或者需要将不同的集群数据实现 数据的 Federation，可以采用跨集群搜索（CCS）
- Tribe Node 和 Cross Cluster Search 的比较，推荐在新版本中使用 CCS
- 如何配置并使用 Cross Cluster Search 查询数据

相关阅读

- 在Kibana中使用Cross data search https://kelonsoftware.com/cross-cluster-search-kibana/

#### 37 | 集群分布式模型及选主与脑裂问题

##### 分布式特性

Elasticsearch 的分布式架构带来的好处

- 存储的水平扩容，支持 PB 级数据
- 提高系统的可用性，部分节点停止服务，整个集群的服务不受影响

Elasticsearch 的分布式架构

- 不同的集群通过不同的名字来区分，默认名字 “elasticsearch”
- 通过配置文件修改，或者在命令行中 -E cluster.name=geektime 进行设定

节点

节点是一个 Elasticsearch 的实例

- 其本质上就是一个 JAVA 进程
- 一台机器上可以运行多个 Elasticsearch 进程，但是生产环境一般建议一台机器上就运行一个 Elasticsearch 实例

每一个节点都有名字，通过配置文件配置，或者启动时候 -E node.name=geektime 指定

每一个节点在启动之后，会分配一个 UID，保存在 data 目录下

Coordinating Node

处理请求的节点，叫 Coordinating Node；路由请求到正确的节点，例如创建索引的请求，需要路由到 Master 节点

所有节点默认都是 Coordinating Node

通过将其他类型设置成 False，使其成为 Dedicated Coordinating Node

##### Demo - 启动节点，Cerebro介绍

启动一个节点的



Code



```
 
```

https://github.com/lmenezes/cerebro/releases

- Overview /Filter by node / index
- Nodes
- REST / More
- Health Status



##### 多节点启动

启动es

Code

```
bin/elasticsearch -E node.name=node1 -E cluster.name=geektime -E path.data=node1_data -d
bin/elasticsearch -E node.name=node2 -E cluster.name=geektime -E path.data=node2_data -d
bin/elasticsearch -E node.name=node3 -E cluster.name=geektime -E path.data=node3_data -d
```

部署Cerebro



Code



```
wget -O /opt/src/cerebro-0.9.3.tgz https://github.com/lmenezes/cerebro/releases/download/v0.9.3/cerebro-0.9.3.tgz 
tar xf cerebro-0.9.3.tgz -C /opt/
ln -s /opt/cerebro-0.9.3 /opt/cerebro
```

启动Cerebor



Code



```
cd /opt/cerebro
bin/cerebro
```

Demo - 创建一个新的索引

发送创建索引的请求

- Settings 3 Primary 和 1 个 Replica
- 请求可以发送到任何的节点，处理你请求的节点，叫做 Coordinating Node
- 创建 / 删除 索引的请求，只能被 Master 节点处理

cerebor创建索引

more -> create index -> 设置名称，分片，副本数 -> Create

##### 分布式组件

Data Node

可以保存数据的节点，叫做 Data Node；节点启动后，默认就是数据节点。可以设置 node.data: false 禁止

Data Node的职责：保存分片数据。在数据扩展上起到了至关重要的作用（由 Master Node 决定如何把 分片分发到数据节点上）

通过增加数据节点：可以解决数据水平扩展和解决数据单点问题

Master Node

Master Node 的职责

- 处理创建，删除索引等请求 /决定分片被分配到哪个节点 / 负责索引的创建与删除
- 维护并且更新 Cluster State

Master Node 的最佳实践

- Master 节点非常重要，在部署上需要考虑解决单点的问题
- 为一个集群设置多个 Master 节点 / 每个节点只承担 Master 的单一⻆色

Master Eligible Nodes & 选主流程

一个集群，支持配置多个 Master Eligible 节点。这些节点可以在必要时(如 Master 节点出现故障，网络故障时)参与选主流程，成为 Master 节点

每个节点启动后，默认就是一个 Master eligible 节点；可以设置 node.master: false 禁止

当集群内第一个 Master eligible 节点启动时候，它会将自己选举成 Master 节点

集群状态

- 集群状态信息（Cluster State），维护了一个集群中，必要的信息
  - 所有的节点信息
  - 所有的索引和其相关的 Mapping 与 Setting 信息
  - 分片的路由信息
- 在每个节点上都保存了集群的状态信息
- 但是，只有 Master 节点才能修改集群的状态信息，并负责同步给其他节点；因为，任意节点都能修改信息会导致 Cluster State 信息的不一致

Demo - 增加一个新的节点

- bin/elasticsearch -E node.name=node2 -E cluster.name=geektime -E path.data=node2_data -E http.port=9201
- Nodes API 看到新增节点
- 发现 Replica 被分配

Master Eligible Nodes & 选主的过程

- 互相 Ping 对方，Node Id 低的会成为被选举的节点
- 其他节点会加入集群，但是不承担 Master 节点的⻆色。一旦发现被选中的主节点丢失， 就会选举出新的 Master 节点

脑裂问题

Split-Brain，分布式系统的经典网络问题，当出现网络问题，一个节点和其他节点无法连接

- Node 2 和 Node 3 会重新选举 Master
- Node 1 自己还是作为 Master，组成一个集群，同时更新 Cluster State
- 导致 2 个 master，维护不同的 cluster state。当网络恢复时，无法选择正确恢复

如何避免脑裂问题

限定一个选举条件，设置 quorum(仲裁)，只有在 Master eligible 节点数大于 quorum 时，才能进行选举

- 限定一个选举条件，设置 quorum(仲裁)，只有在 Master eligible 节点数大于 quorum 时，才能进行选举
- 当 3 个 master eligible 时，设置 discovery.zen.minimum_master_nodes 为 2，即可避免脑裂

从 7.0 开始，无需这个配置

- 移除 minimum_master_nodes 参数，让Elasticsearch自己选择可以形成仲裁的节点。
- 典型的主节点选举现在只需要很短的时间就可以完成。集群的伸缩变得更安全、更容易，并且可能造成丢失数据的系统配置选项更少了
- 节点更清楚地记录它们的状态，有助于诊断为什么它们不能加入集群或为什么无法选举出主节点

配置节点类型

一个节点默认情况下是一个 Master eligible，data and ingest node:

| 节点类型          | 配置参数    | 默认值                      |
| ----------------- | ----------- | --------------------------- |
| maste eligible    | node.master | true                        |
| data              | node.data   | true                        |
| ingest            | node.ingest | ture                        |
| coordinating only | 无          | 设置上面三个参数全部为false |
| machine learning  | node.ml     | true (需要enable x-pack)    |

本节知识点回顾

- Elasticsearch 天生的分布式架构。为了实现实现数据可用性
  - 部署多台 Data Nodes，可以实现数据存储的水平扩展
- 提高服务可用性
  - Master 节点非常重要。设置多台 Master Eligible Nodes，同时 设置合理的 quorum 数，避免脑裂问题
  - 设置多台 Coordinating Node，提升查询的可用性和性能

相关阅读

- https://www.elastic.co/cn/blog/a-new-era-for-cluster-coordination-in-elasticsearch

## 38 | 分片与集群的故障转移

### 集群的分片

Primary Shard - 提升系统存储容量

分片是 Elasticsearch 分布式存储的基石

- 主分片 / 副本分片

通过主分片，将数据分布在所有节点上

- Primary Shard，可以将一份索引的数据，分散在多个 Data Node 上，实现存储的水平扩展
- 主分片(Primary Shard)数在索引创建时候指定，后续默认不能修改，如要修改，需重建索引

Primary Shard - 提升数据可用性

数据可用性：通过引入副本分片 (Replica Shard) 提高数据的可用性。一旦主分片丢失，副本分片可以 Promote 成主分片。副本分片数可以动态调整。每个节点上都有完备的数据。如果不设置副本分片，一旦出现节点硬件故障，就有可能造成数据丢失

提升系统的读取性能：副本分片由主分片(Primary Shard)同步。通过支持增加 Replica 个数，一定程度可以提高读取的吞吐量

分片数的设定

如何规划一个索引的主分片数和副本分片数？

- 主分片数过小：例如创建了 1 个 Primary Shard 的 Inde； 如果该索引增⻓很快，集群无法通过增加节点实现对这个索引的数据扩展
- 主分片数设置过大：导致单个 Shard 容量很小，引发一个节点上有过多分片，影响性能
- 副本分片数设置过多，会降低集群整体的写入性能

### 故障转移

单节点的集群

副本无法分片，集群状态为黄色

增加一个数据节点, 总共2个节点

- 集群状态转为绿色
- 集群具备故障转移能力
- 尝试着将 Replica 设置成 2 和 3， 查看集群的状况

再增加一个数据节点, 总共3个节点

- 集群具备故障转移能力
- Master 节点会决定分片分配到哪个节点
- 通过增加节点，提高集群的计算能力

故障转移

- 3 个节点共同组成。包含了 1 个索引，索引设置了 3 个 Primary Shard 和 1 个 Replica
- 节点 1 是 Master 节点，节点意外出现故障。集群重新选举 Master 节点
- Node 3 上的 R0 提升成 P0，集群变⻩
- R0 和 R1 分配，集群变绿

集群健康状态

查看集群健康状态 - GET /_cluster/health

- Green：健康状态，所有的主分片和副本分片都可用
- Yellow：亚健康，所有的主分片可用，部分副本分片不可
- 用Red：不健康状态，部分主分片不可用

### Demo

1. 启动一个节点，3 个 Primary shard，一个 Replica，集群⻩色，因为无法分配 Replica
2. 启动三个节点，1 个索引上包含 3 个 Primary Shard，一个 Replica
3. 关闭 Node 1（Master）
4. 查看 Master Node 重新选举
5. 集群变⻩，然后重新分配

本节知识点回顾

- 主分片，副本分片的作用
  - 主分片的分片数，设置后不能修改，除非重新索引数据
  - 副本分片可以随时修改
- 集群的故障转移
  - 需要集群具备故障转移的能力，必须将索引的副本分片数设置为 1，否则，一点有节点就是，就会造成数据丢失

## 39 | 文档分布式存储

文档存储在分片上

文档会存储在具体的某个主分片和副本分片上：例如 文档 1， 会存储在 P0 和 R0 分片上

文档到分片的映射算法

- 确保文档能均匀分布在所用分片上，充分利用硬件资源，避免部分机器空闲，部分机器繁忙
- 潜在的算法
  - 随机 / Round Robin。当查询文档 1，分片数很多，需要多次查询才可能查到 文档 1
  - 维护文档到分片的映射关系，当文档数据量大的时候，维护成本高
  - 实时计算，通过文档 1，自动算出，需要去那个分片上获取文档

文档到分片的路由算法

算法：shard = hash(_routing) % number_of_primary_shards

- Hash 算法确保文档均匀分散到分片中
- 默认的 _routing 值是文档 id
- 可以自行制定 routing数值，例如用相同国家的商品，都分配到指定的 shard
- 设置 Index Settings 后， Primary 数，不能随意修改的根本原因

更新一个文档

[![image-20210108214959293](http://wangzhangtao.com/img/body/Elasticsearch%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AF%E4%B8%8E%E5%AE%9E%E6%88%98/image-20210108214959293.png)](https://wangzhangtao.com/img/body/Elasticsearch核心技术与实战/image-20210108214959293.png)

image-20210108214959293

删除一个文档

[![image-20210108215113774](http://wangzhangtao.com/img/body/Elasticsearch%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AF%E4%B8%8E%E5%AE%9E%E6%88%98/image-20210108215113774.png)](https://wangzhangtao.com/img/body/Elasticsearch核心技术与实战/image-20210108215113774.png)

image-20210108215113774

本节知识点回顾

1. 可以通过设置 Index Settings，控制数据的分片
2. Primary Shard 的值不能修改，修改需要重新 Index。默认值是 5， 从 7 开始，默认值改为 1
3. 索引写入数据后，Replica 的值可以修改。增加副本，可提高大并发下的读取性能
4. 通过控制集群的节点数，设置 Primary Shard 数，实现水平扩展

## 40 | 分⽚及其⽣命周期

### 分片的内部原理

> 什么是 ES 的分片? ES 中最小的工作单元 / 是一个 Lucene 的 Index

一些问题：

- 为什么 ES 的搜索是近实时的(1 秒后被搜到)
- ES 如何保证在断电时数据也不会丢失
- 为什么删除文档，并不会立刻释放空间

倒排索引不可变性

倒排索引采用 Immutable Design，一旦生成，不可更改

不可变性，带来了的好处如下：

- 无需考虑并发写文件的问题，避免了锁机制带来的性能问题
- 一旦读入内核的文件系统缓存，便留在哪里。只要文件系统存有足够的空间，大部分请求就会直接请求内存，不会命中磁盘，提升了很大的性能
- 缓存容易生成和维护 / 数据可以被压缩

不可变更性，带来了的挑战：如果需要让一个新的文档可以被搜索，需要重建整个索引。

### 分片存储的几个概念-写入流程

> Lucene Index

在 Lucene 中，单个倒排索引文件被称为Segment。Segment 是自包含的，**不可变更**的。多个 Segments 汇总在一起，称为 Lucene 的Index，其对应的就是 ES 中的 Shard

当有新文档写入时，会生成新 Segment，查询时会同时查询所有 Segments，并且对结果汇总。Lucene 中有一个文件，用来记录所有Segments 信息，叫做 Commit Point

删除的文档信息，保存在“.del”文件中

![image-20220221163651155](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220221163651155.png)

> 什么是 Refresh

将 Index buffer[内存空间] 写入 Segment 的过程叫Refresh。Refresh 不执行 fsync 操作 

Refresh 频率：默认 1 秒发生一次，可通过index.refresh_interval 配置。Refresh 后， 数据就可以被搜索到了。这也是为什么Elasticsearch 被称为近实时搜索

如果系统有大量的数据写入，那就会产生很多的 Segment

Index Buffer 被占满时，会触发 Refresh，默认值是 JVM 的 10%

![image-20220221163730453](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220221163730453.png)

> 什么是 Transaction Log

Segment 写入磁盘的过程相对耗时，借助文件系统缓存，Refresh 时，先将Segment 写入缓存以开放查询

为了保证数据不会丢失。所以在 Index 文档时，同时写 Transaction Log，高版本开始，Transaction Log 默认落盘。每个分片有一个 Transaction Log

在 ES Refresh 时，Index Buffer 被清空， Transaction log 不会清空

![image-20220221163844805](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220221163844805.png)



> 什么是 Flush

ES Flush & Lucene Commit

- 调用 Refresh，Index Buffer 清空并且 Refresh
- 调用 fsync，将缓存中的 Segments 写入磁盘
- 清空（删除）Transaction Log
- 默认 30 分钟调用一次
- Transaction Log 满 （默认 512 MB）

![image-20220221164530421](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220221164530421.png)

> Merge

Segment 很多，需要被定期被合并; 减少 Segments / 删除已经删除的文档

ES 和 Lucene 会自动进行 Merge 操作; 命令 `POST my_index/_forcemerge`



### 本节知识点回顾

- Shard 和 Lucene Index
  - Index Buffer / Segment / Transaction Log
- Refresh & Flush
- Merge

## 41 | 剖析分布式查询及相关性算分

分布式搜索的运行机制

Elasticsearch 的搜索，会分两阶段进行

- 第一阶段 - Query
- 第二阶段 - Fetch

Query-then-Fetch

> Query 阶段

用户发出搜索请求到 ES 节点。节点收到请求后， 会以 Coordinating 节点的身份，在 6 个主副分片中随机选择 3 个分片，发送查询请求

被选中的分片执行查询，进行排序。然后，每个分片都会返回 From + Size 个排序后的文档 Id 和排序值 给 Coordinating 节点

![image-20220221165413939](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220221165413939.png)

> Fetch 阶段

Coordinating Node 会将 Query 阶段，从每个分片获取的排序后的文档 Id 列表重新进行排序。选取 From 到 From + Size 个文档的 Id

以 multi get 请求的方式，到相应的分片获取详细的文档数据

![image-20220221170034550](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220221170034550.png)



> Query Then Fetch 潜在的问题

性能问题

- 每个分片上需要查的文档个数 = from + size
- 最终协调节点需要处理：number_of_shard * ( from+size )
- 深度分⻚

相关性算分：每个分片都基于自己的分片上的数据进行相关度计算。这会导致打分偏离的情况，特别是数据量很少时。相关性算分在分片之间是相互独立。当文档总数很少的情况下，如果主分片大于 1，主分片数越多 ，相关性算分会越不准

> 解决算分不准的方法

数据量不大的时候，可以将主分片数设置为 1

当数据量足够大时候，只要保证文档均匀分散在各个分片上，结果一般就不会出现偏差

> 使 用 DFS Query Then Fetch

- 搜索的URL 中指定参数 “_search?search_type=dfs_query_then_fetch”
- 到每个分片把各分片的词频和文档频率进行搜集，然后完整的进行一次相关性算分， 耗费更加多的 CPU 和内存，执行性能低下，一般不建议使用

> 相关性算分问题 Demo

1. 写入 3 条记录 “Good” / “Good morning” / “good morning everyone”
2. 使用 1 个主分片测试， Good 应该排在第一，Good DF 数值应该是 3
3. 和 20 个主分片，测试
4. 当 多个 个主分片时，3 个文档的算分都一样。 可以通过 Explain API 进行分析
5. 在 3 个主分片上 执行 DFS Query Then Fetch，结果和一个分片上一致

> 使用1个主分片测试

Code

```sh
DELETE message

POST message/_doc?routing=1
{ "content":"good" }

POST message/_doc?routing=2
{  "content":"good morning" }

POST message/_doc?routing=3
{ "content":"good morning everyone" }

POST message/_search
{
  "explain": true,
  "query": {
    "term": {
      "content": {
        "value": "good"
      }
    }
  }
}
```

> 使用20个主分片测试



Code



```sh
DELETE message
PUT message
{
  "settings": {
    "number_of_shards": 20
  }
}

GET message

POST message/_doc?routing=1
{ "content":"good" }

POST message/_doc?routing=2
{  "content":"good morning" }

POST message/_doc?routing=3
{ "content":"good morning everyone" }

POST message/_search
{
  "explain": true,
  "query": {
    "term": {
      "content": {
        "value": "good"
      }
    }
  }
}

POST message/_search?search_type=dfs_query_then_fetch
{
  "query": {
    "term": {
      "content": {
        "value": "good"
      }
    }
  }
}
```

本节知识点回顾

- 学习了分布式搜索 Query then Fetch 的机制
  - Why / How
- Query Then Fetch 带来的潜在问题
  - 深度分⻚：使用 Search After
  - 算分不准 ：设置 1 个主分片 / 数据量大时，只需要保证文档平均分布 / DFS Query then Fetch

## 42 | 排序及 Doc Values & Field Data

排序

Elasticsearch 默认采用相关性算分对结果进行降序排序

可以通过设定 sorting 参数，自行设定排序

如果不指定_score，算分为 Null



Code



```sh
#单字段排序
POST /kibana_sample_data_ecommerce/_search
{
  "size": 5,
  "query": {
    "match_all": {

    }
  },
  "sort": [
    {"order_date": {"order": "desc"}}
  ]
}
```

多字段进行排序

- 组合多个条件
- 优先考虑写在前面的排序
- 支持对相关性算分进行排序



Code



```shell
#多字段排序
POST /kibana_sample_data_ecommerce/_search
{
  "size": 5,
  "query": {
    "match_all": {

    }
  },
  "sort": [
    {"order_date": {"order": "desc"}},
    {"_doc":{"order": "dasc"}},
    {"_score":{ "order": "desc"}}
  ]
}
```

Demo

- Elasticsearch 默认对查询结果的相关性算分进行降序排序
- 用户可以设定对单个 sorting 参数，自行设定排序。如果不对算分进行排序。_score 为 null
- 支持多个字段排序

对 Text 类型排序



Code



```shell
GET kibana_sample_data_ecommerce/_mapping

#对 text 字段进行排序。默认会报错，需打开fielddata
POST /kibana_sample_data_ecommerce/_search
{
  "size": 5,
  "query": {
    "match_all": {

    }
  },
  "sort": [
    {"customer_full_name": {"order": "desc"}}
  ]
}
```

> 排序的过程

- 排序是针对字段原始内容进行的。 倒排索引无法发挥作用
- 需要用到正排索引。通过文档 Id 和字段快速得到字段原始内容
- Elasticsearch 有两种实现方法
  - Fielddata
  - Doc Values （列式存储，对 Text 类型无效）

> Doc Values vs Field Data

|          | Doc Values                     | Field data                                      |
| -------- | ------------------------------ | ----------------------------------------------- |
| 何时创建 | 索引时，和倒排索引一起创建     | 搜索时候动态创建                                |
| 创建位置 | 磁盘文件                       | JVM Heap                                        |
| 优点     | 避免大量内存占用               | 索引速度快，不占用额外的磁盘空间                |
| 缺点     | 降低索引速度，占用额外磁盘空间 | 文档过多时，动态创建开销大，占用 过多JVM Heap， |
| 缺省值   | ES 2.x 之后                    | ES 1.x 及之前                                   |

Demo

- 单字段多字段排序
- 对 Text 和 Keyword 类型进行排序

> 打开 Fielddata

- 默认关闭，可以通过 Mapping 设置打开。修改设置后，即时生效，无需重建索引
- 其他字段类型不支持，只支持对 Text 进行设定
- 打开后，可以对 Text 字段进行排序。但是是对分词后的 term 排序，所以，结果往往无法满足预期，不建议使用
- 部分情况下打开，满足一些聚合分析的特定需求



Code



```sh
#打开 text的 fielddata
PUT kibana_sample_data_ecommerce/_mapping
{
  "properties": {
    "customer_full_name" : {
          "type" : "text",
          "fielddata": true,
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        }
  }
}
```

> 关闭 Doc Values

默认启用，可以通过 Mapping 设置关闭，增加索引的速度 / 减少磁盘空间

如果重新打开，需要重建索引

什么时候需要关闭：明确不需要做排序及聚合分析



Code



```shell
#关闭 keyword的 doc values
PUT test_keyword
PUT test_keyword/_mapping
{
  "properties": {
    "user_name":{
      "type": "keyword",
      "doc_values":false
    }
  }
}

DELETE test_keyword

PUT test_text
PUT test_text/_mapping
{
  "properties": {
    "intro":{
      "type": "text",
      "doc_values":true
    }
  }
}

DELETE test_text
```

获取 Doc Values & Fielddata 中存储的内容

- Text 类型的不支持 Doc Values
- Text 类型打开 Fielddata后，可以查看分词后的数据



Code



```shell
DELETE temp_users
PUT temp_users
PUT temp_users/_mapping
{
  "properties": {
    "name":{"type": "text","fielddata": true},
    "desc":{"type": "text","fielddata": true}
  }
}

Post temp_users/_doc
{"name":"Jack","desc":"Jack is a good boy!","age":10}

#打开fielddata 后，查看 docvalue_fields数据
POST  temp_users/_search
{
  "docvalue_fields": [
    "name","desc"
    ]
}

#查看整型字段的docvalues
POST  temp_users/_search
{
  "docvalue_fields": [
    "age"
    ]
}
```

Fielddata Demo

- 对 Text 字段设置 fielddata，支持随时修改
- Doc Values 可以在 Mapping 中关闭。但是需要重新索引
- Text 不支持 Doc Values
- 使用 docvalue_fields 查看存储的信息

本节知识点回顾

- Elasticsearch 支持自定义排序
- Doc Values 和 Fielddata 的对比
- 如何设置 Doc Values 和 Fielddata

## 43 | 分⻚与遍历 – From，Size，Search After & Scroll API

From / Size

默认情况下，查询按照相关度算分排序，返回前10 条记录

容易理解的分⻚方案:

- From：开始位置
- Size：期望获取文档的总数

分布式系统中深度分页的问题

ES 天生就是分布式的。查询信息，但是数据分别保存在多个分片，多台机器上，ES 天生就需要满足排序的需要（按照相关性算分）

当一个查询： From = 990， Size =10

1. 会在每个分片上先都获取 1000 个文档。然后， 通过 Coordinating Node 聚合所有结果。最后再通过排序选取前 1000 个文档
2. ⻚数越深，占用内存越多。为了避免深度分⻚带来的内存开销。ES 有一个设定，默认限定到10000 个文档 (参数 Index.max_result_window)

From / Size Demo

- 简单的 From / Size demo
- From + Size 必须小与 10000



Code



```
POST tmdb/_search
{
  "from": 10000,
  "size": 1,
  "query": {
    "match_all": {

    }
  }
}
```

##### Search After 避免深度分页的问题

避免深度分⻚的性能问题，可以实时获取下一⻚文档信息, 缺点是

- 不支持指定⻚数（From）
- 只能往下翻

第一步搜索需要指定 sort，并且保证值是唯一的（可以通过加入 _id 保证唯一性）；然后使用上一次，最后一个文档的 sort 值进行查询



Code



```
#Scroll API
DELETE users

POST users/_doc
{"name":"user1","age":10}

POST users/_doc
{"name":"user2","age":11}


POST users/_doc
{"name":"user3","age":12}

POST users/_doc
{"name":"user4","age":13}

POST users/_count

POST users/_search
{
    "size": 1,
    "query": {
        "match_all": {}
    },
    "sort": [
        {"age": "desc"} ,
        {"_id": "asc"}    
    ]
}

POST users/_search
{
    "size": 1,
    "query": {
        "match_all": {}
    },
    "search_after":
        [ 13, "7P5M5nYBRcT7yup-OCwn" ],
    "sort": [
        {"age": "desc"} ,
        {"_id": "asc"}    
    ]
}
```

Search After 是如何解决深度分页的问题

- 假 定 Size 是 10
- 当查询 990 – 1000
- 通过唯一排序值定位，将每次要处理的文档数都控制在 10

Scroll API

- 创建一个快照，有新的数据写入以后，无法被查到
- 每次查询后，输入上一次的 Scroll Id



Code



```
# 获取到镜像的 scroll_id
POST /users/_search?scroll=5m
{
    "size": 1,
    "query": {
        "match_all" : {
        }
    }
}

POST users/_doc
{"name":"user5","age":50}
POST /_search/scroll
{
    "scroll" : "1m",
    "scroll_id" : "DXF1ZXJ5QW5kRmV0Y2gBAAAAAAAADo0WRW9xSXI2ek1UTHVhWEUzX3Y5eGluZw=="
}
```

Demo for Scroll API

1. 插入 4 条记录
2. 调用 Scroll API
3. 插入一条新的记录
4. 发现只能查到 4 条数据

不同的搜索类型和使用场景

- Regular：需要实时获取顶部的部分文档。例如查询最新的订单
- Scroll: 需要全部文档，例如导出全部数据
- Pagination: From 和 Size, 如果需要深度分⻚，则选用 Search After

本节知识点回顾

- Elasticsearch 默认返回 10 个结果
- 为了获取更多的结果，提供 3 种方式解决分⻚与遍历
  - From / Size 的用法，深度分⻚所存在的问题
  - Search After 解决深度分⻚的问题
  - Scroll API，通过快照，遍历数据

## 44 | 处理并发读写操作

并发控制的必要性

两个 Web 程序同时更新某个文档，如果缺乏有效的并发，会导致更改的数据丢失

悲观并发控制：

- 假定有变更冲突的可能。会对资源加锁，防止冲突。
- 例如数据库行锁

乐观并发控制：

- 假定冲突是不会发生的，不会阻塞正在尝试的操作。如果数据在读写中被修改，更新将会失败。应用程序决定如何解决冲突，例如重试更新，使用新的数据，或者将错误报告给用户。
- ES 采用的是乐观并发控制

ES 的乐观并发控制

- ES 中的文档是不可变更的。如果你更新一个文档，会将就文档标记为删除，同时增加一个全新的文档。同时文档的 version 字段加 1
- 内部版本控制：If_seq_no + If_primary_term
- 使用外部版本(使用其他数据库作为主要数据存储)：version + version_type=external



Code



```
DELETE products
PUT products

PUT products/_doc/1
{
  "title":"iphone",
  "count":100
}
GET products/_doc/1

# 内部版本控制：If_seq_no + If_primary_term
PUT products/_doc/1?if_seq_no=0&if_primary_term=1
{
  "title":"iphone",
  "count":101
}
GET products/_doc/1

# 外部版本控制：version + version_type=external
PUT products/_doc/1?version=3&version_type=external
{
  "title":"iphone",
  "count":102
}
GET products/_doc/1
```

本节知识点回顾

- ES 采用的是乐观并发控制
- 在需要控制并发的场景，通过指定 If_seq_no 和 If_primary_term
- 外部版版本 version & version_type=external



## 45 | Bucket & Metric 聚合分析及嵌套聚合

### Bucket & Metric Aggregation

- Metric - 一些系列的统计方法
- Bucket - 一组满足条件的文档

Aggregation 的语法

Aggregation 属于 Search 的 一部分。一般情况下，建议将其 Size 指定为 0

[![image-20210110123451170](https://wangzhangtao.com/img/body/Elasticsearch%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AF%E4%B8%8E%E5%AE%9E%E6%88%98/image-20210110123451170.png)](https://wangzhangtao.com/img/body/Elasticsearch核心技术与实战/image-20210110123451170.png)

image-20210110123451170

### 具体案例

#### 一个例子：工资统计信息



Code



```shell
DELETE /employees
PUT /employees/
{
  "mappings" : {
      "properties" : {
        "age" : {
          "type" : "integer"
        },
        "gender" : {
          "type" : "keyword"
        },
        "job" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 50
            }
          }
        },
        "name" : {
          "type" : "keyword"
        },
        "salary" : {
          "type" : "integer"
        }
      }
    }
}

PUT /employees/_bulk
{ "index" : {  "_id" : "1" } }
{ "name" : "Emma","age":32,"job":"Product Manager","gender":"female","salary":35000 }
{ "index" : {  "_id" : "2" } }
{ "name" : "Underwood","age":41,"job":"Dev Manager","gender":"male","salary": 50000}
{ "index" : {  "_id" : "3" } }
{ "name" : "Tran","age":25,"job":"Web Designer","gender":"male","salary":18000 }
{ "index" : {  "_id" : "4" } }
{ "name" : "Rivera","age":26,"job":"Web Designer","gender":"female","salary": 22000}
{ "index" : {  "_id" : "5" } }
{ "name" : "Rose","age":25,"job":"QA","gender":"female","salary":18000 }
{ "index" : {  "_id" : "6" } }
{ "name" : "Lucy","age":31,"job":"QA","gender":"female","salary": 25000}
{ "index" : {  "_id" : "7" } }
{ "name" : "Byrd","age":27,"job":"QA","gender":"male","salary":20000 }
{ "index" : {  "_id" : "8" } }
{ "name" : "Foster","age":27,"job":"Java Programmer","gender":"male","salary": 20000}
{ "index" : {  "_id" : "9" } }
{ "name" : "Gregory","age":32,"job":"Java Programmer","gender":"male","salary":22000 }
{ "index" : {  "_id" : "10" } }
{ "name" : "Bryant","age":20,"job":"Java Programmer","gender":"male","salary": 9000}
{ "index" : {  "_id" : "11" } }
{ "name" : "Jenny","age":36,"job":"Java Programmer","gender":"female","salary":38000 }
{ "index" : {  "_id" : "12" } }
{ "name" : "Mcdonald","age":31,"job":"Java Programmer","gender":"male","salary": 32000}
{ "index" : {  "_id" : "13" } }
{ "name" : "Jonthna","age":30,"job":"Java Programmer","gender":"female","salary":30000 }
{ "index" : {  "_id" : "14" } }
{ "name" : "Marshall","age":32,"job":"Javascript Programmer","gender":"male","salary": 25000}
{ "index" : {  "_id" : "15" } }
{ "name" : "King","age":33,"job":"Java Programmer","gender":"male","salary":28000 }
{ "index" : {  "_id" : "16" } }
{ "name" : "Mccarthy","age":21,"job":"Javascript Programmer","gender":"male","salary": 16000}
{ "index" : {  "_id" : "17" } }
{ "name" : "Goodwin","age":25,"job":"Javascript Programmer","gender":"male","salary": 16000}
{ "index" : {  "_id" : "18" } }
{ "name" : "Catherine","age":29,"job":"Javascript Programmer","gender":"female","salary": 20000}
{ "index" : {  "_id" : "19" } }
{ "name" : "Boone","age":30,"job":"DBA","gender":"male","salary": 30000}
{ "index" : {  "_id" : "20" } }
{ "name" : "Kathy","age":29,"job":"DBA","gender":"female","salary": 20000}
```

#### Metric Aggregation

- 单值分析：只输出一个分析结果
  - min, max, avg, sum
  - Cardinality （类似 distinct Count）
- 多值分析：输出多个分析结果
  - stats, extended_stats
  - percentile, percentile rank
  - top hits （排在前面的示例）

Metric 聚合的具体 Demo

- 查看最低工资
- 查看最高工资
- 一个聚合输出多个值
- 一次查询包含多个聚合：同时查看最低，最高和平均工资



Code



```
# Metric 聚合，找到最低的工资
POST employees/_search
{
  "size": 0,
  "aggs": {
    "min_salary": {
      "min": {
        "field": "salary"
      }
    }
  }
}

# 多个 Metric 聚合，找到最低最高和平均工资
POST employees/_search
{
  "size": 0,
  "aggs": {
    "max_salary": {
      "max": {
        "field": "salary"
      }
    },
    "min_salary": {
      "min": {
        "field": "salary"
      }
    },
    "avg_salary": {
      "avg": {
        "field": "salary"
      }
    }
  }
}

# 一个聚合，输出多值
POST employees/_search
{
  "size": 0,
  "aggs": {
    "stats_salary": {
      "stats": {
        "field": "salary"
      }
    }
  }
}
```

#### Bucket

按照一定的规则，将文档分配到不同的桶中，从而达到分类的目的。ES 提供的一些常⻅的 Bucket Aggregation

- Terms
- 数字类型
  - Range / Data Range
  - Histogram / Date Histogram

支持嵌套：也就在桶里再做分桶

#### Terms Aggregation

字段需要打开 fielddata，才能进行 Terms Aggregation

- Keyword 默认支持 doc_values
- Text 需要在 Mapping 中 enable。会按照分词后的结果进行分

Demo

- 对 job 和 job.keyword 进行聚合
- 对性别进行 Terms 聚合
- 指定 bucket size

对 job 和 job.keyword 进行聚合



Code



```
# 对keword 进行聚合
POST employees/_search
{
  "size": 0,
  "aggs": {
    "jobs": {
      "terms": {
        "field": "job.keyword"      
      }
    }
  }
}

# 对 Text 字段进行 terms 聚合查询，失败
POST employees/_search
{
  "size": 0,
  "aggs": {
    "jobs": {
      "terms": {
        "field": "job"      
      }
    }
  }
}

POST employees/_mapping
{
  "properties": {
    "job":{
      "type": "text",
      "fielddata": true
    }
  }
}

# 对 Text 字段进行 terms 分词。分词后的terms
POST employees/_search
{
  "size": 0,
  "aggs": {
    "jobs": {
      "terms": {
        "field":"job"
      }
    }
  }
}
```

对性别进行 Terms 聚合



Code



```
# 对 性别的 keyword 进行聚合
POST employees/_search
{
  "size": 0,
  "aggs": {
    "gender": {
      "terms": {
        "field": "gender"
      }
    }
  }
}
```

指定 bucket size



Code



```
#指定 bucket 的 size
POST employees/_search
{
  "size": 0,
  "aggs": {
    "ages_5": {
      "terms": {
        "field": "age",
        "size": 5
      }
    }
  }
}
```

#### Cardinality

类似 SQL 中的 Distinct



Code



```
# 对job.keyword 和 job 进行 terms 聚合，分桶的总数并不一样
POST employees/_search
{
  "size": 0,
  "aggs": {
    "cardinate": {
      "cardinality": {
        "field": "job.keyword"
      }
    }
  }
}
```

Bucket Size & Top Hits Demo

- 应用场景：当获取分桶后，桶内最匹配的顶部文档列表
- Size：按年龄分桶，找出指定数据量的分桶信息
- Top Hits：查看各个工种中，年纪最大的 3 名员工



Code



```
# 指定size，不同工种中，年纪最大的3个员工的具体信息
POST employees/_search
{
  "size": 0,
  "aggs": {
    "jobs": {
      "terms": {
        "field":"job.keyword"
      },
      "aggs":{
        "old_employee":{
          "top_hits":{
            "size":3,
            "sort":[
              {
                "age":{
                  "order":"desc"
                }
              }
            ]
          }
        }
      }
    }
  }
}
```

### 优化 Terms 聚合的性能

https://[www.elastic.co/guide/en/elasticsearch/reference/7.1/tune-for-search-speed.html](http://www.elastic.co/guide/en/elasticsearch/reference/7.1/tune-for-search-speed.html)

[![image-20210110123723713](http://wangzhangtao.com/img/body/Elasticsearch%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AF%E4%B8%8E%E5%AE%9E%E6%88%98/image-20210110123723713.png)](https://wangzhangtao.com/img/body/Elasticsearch核心技术与实战/image-20210110123723713.png)

image-20210110123723713

优化命令：”eager_global_ordinals”: true

使用场景：需要对聚合性能要求较高

Range & Hisogram 聚合

- 按照数字的范围，进行分桶
- 在 Range Aggregation 中，可以自定义 Key
- Demo:
  - 按照工资的 Range 分桶
  - 按照工资的间隔（Histogram）分桶



Code



```
#Salary Ranges 分桶，可以自己定义 key
POST employees/_search
{
  "size": 0,
  "aggs": {
    "salary_range": {
      "range": {
        "field": "salary",
        "ranges": [
          {
            "from": 0,
            "to": 10000
          },
          {
            "from": 10000,
            "to": 20000
          },
          {
            "key": "> 20000",
            "from": 20000
          }
        ]
      }
    }
  }
}


#Salary Histogram,工资0到10万，以 5000一个区间进行分桶
POST employees/_search
{
  "size": 0,
  "aggs": {
    "salary_histrogram": {
      "histogram": {
        "field":"salary",
        "interval":5000,
        "extended_bounds":{
          "min":0,
          "max":100000
        }
      }
    }
  }
}
```

### 嵌套聚合 Bucket + Metric Aggregation

Bucket 聚合分析允许通过添加子聚合分析来进一步分析，子聚合分析可以是: Bucket 和 Metric

Demo：

- 按照工作类型进行分桶，并统计工资信息
- 先按照工作类型分桶，然后按性别分桶，并统计工资信息



Code



```
# 嵌套聚合1，按照工作类型分桶，并统计工资信息
POST employees/_search
{
  "size": 0,
  "aggs": {
    "Job_salary_stats": {
      "terms": {
        "field": "job.keyword"
      },
      "aggs": {
        "salary": {
          "stats": {
            "field": "salary"
          }
        }
      }
    }
  }
}

# 多次嵌套。根据工作类型分桶，然后按照性别分桶，计算工资的统计信息
POST employees/_search
{
  "size": 0,
  "aggs": {
    "Job_gender_stats": {
      "terms": {
        "field": "job.keyword"
      },
      "aggs": {
        "gender_stats": {
          "terms": {
            "field": "gender"
          },
          "aggs": {
            "salary_stats": {
              "stats": {
                "field": "salary"
              }
            }
          }
        }
      }
    }
  }
}
```

本节知识点

- 聚合分析的具体语法：一个聚合查询中可以包含多个聚合； 每个 Bucket 聚合可以包含子聚合
- Metrix：单值输出 & 多值输出
- Bucket：Terms & 数字范围

相关阅读

- https://www.elastic.co/guide/en/elasticsearch/reference/7.1/search-aggregations-metrics.html
- https://www.elastic.co/guide/en/elasticsearch/reference/7.1/search-aggregations-bucket.html

## 46 | Pipeline 聚合分析

### 一个例子

在员工数最多的工种里，找出平均工资最低的工种

1. 结果和其他的聚合同级
2. min_bucket 求之前结果的最小值
3. 通过 **bucket_path** 关键字指定路径

使用上一章的测试数据



Code



```
# 平均工资最低的工作类型
POST employees/_search
{
  "size": 0,
  "aggs": {
    "jobs": {
      "terms": {
        "field": "job.keyword",
        "size": 10
      },
      "aggs": {
        "avg_salary": {
          "avg": {
            "field": "salary"
          }
        }
      }
    },
    "min_salary_by_job":{
      "min_bucket": {
        "buckets_path": "jobs>avg_salary"
      }
    }
  }
}
```

### Pipeline

管道的概念: 支持对聚合分析的结果，再次进行聚合分析

Pipeline 的分析结果会输出到原结果中，根据位置的不同，分为两类

- Sibling - 结果和现有分析结果同级
  - Max，min，Avg & Sum Bucket
  - Stats，Extended Status Bucket
  - Percentiles Bucket
- Parent - 结果内嵌到现有的聚合分析结果之中
  - Derivative （求导）
  - Cumultive Sum （累计求和）
  - Moving Function (滑动窗口)

Sibling Pipeline 的例子

对不同类型工作的，平均工资

- 求最大
- 平均
- 统计信息
- 百分位数



Code



```
# 平均工资最高的工作类型
POST employees/_search
{
  "size": 0,
  "aggs": {
    "jobs": {
      "terms": {
        "field": "job.keyword",
        "size": 10
      },
      "aggs": {
        "avg_salary": {
          "avg": {
            "field": "salary"
          }
        }
      }
    },
    "max_salary_by_job":{
      "max_bucket": 
      // "avg_bucket": 
      // "stats_bucket": 
      // "percentiles_bucket": 
      {
        "buckets_path": "jobs>avg_salary"
      }
    }
  }
}
```

### Parent Pipeline

#### Derivative 求导

按照年龄，对工资进行求导（看工资发展的趋势）; 采用直方图进行分桶

1. 位置和 avg salary 同级
2. bucket_path 指定为 avg_salary



Code



```
#按照年龄对平均工资求导
POST employees/_search
{
  "size": 0,
  "aggs": {
    "age": {
      "histogram": {
        "field": "age",
        "min_doc_count": 1,
        "interval": 1
      },
      "aggs": {
        "avg_salary": {
          "avg": {
            "field": "salary"
          }
        },
        "derivative_avg_salary":{
          "derivative": {
            "buckets_path": "avg_salary"
          }
        }
      }
    }
  }
}
```

#### Cumulative_sum 求和



Code



```
#Cumulative_sum
POST employees/_search
{
  "size": 0,
  "aggs": {
    "age": {
      "histogram": {
        "field": "age",
        "min_doc_count": 1,
        "interval": 1
      },
      "aggs": {
        "avg_salary": {
          "avg": {
            "field": "salary"
          }
        },
        "cumulative_salary":{
          "cumulative_sum": {
            "buckets_path": "avg_salary"
          }
        }
      }
    }
  }
}
```

#### Moving Function 移动函数



Code



```
#Moving Function
POST employees/_search
{
  "size": 0,
  "aggs": {
    "age": {
      "histogram": {
        "field": "age",
        "min_doc_count": 1,
        "interval": 1
      },
      "aggs": {
        "avg_salary": {
          "avg": {
            "field": "salary"
          }
        },
        "moving_avg_salary":{
          "moving_fn": {
            "buckets_path": "avg_salary",
            "window":10,
            "script": "MovingFunctions.min(values)"
          }
        }
      }
    }
  }
}
```

Parent Pipeline

- 年龄直方图划分的平均工资
  - Cumulative Sum
  - Moving Function

相关阅读

- https://www.elastic.co/guide/en/elasticsearch/reference/7.1/search-aggregations-pipeline.html

## 47 | 聚合的作用范围及排序

### 聚合的作用范围

ES 聚合分析的默认作用范围是 query 的查询结果集

同时 ES 还支持以下方式改变聚合的作用范围

- Filter
- Post_Filter
- Global

使用上一节的测试数据



Code



```
# query
POST employees/_search
{
  "size": 0,
  "query": {
    "range": {
      "age": {
        "gte": 20
      }
    }
  },
  "aggs": {
    "jobs": {
      "terms": {
        "field": "job.keyword"
      }
    }
  }
}
```

### 具体案例

#### Filter

- Filter 只对当前的子聚合语句生效
- all_jobs 还是基于 query 的作用范围



Code



```
#Filter
POST employees/_search
{
  "size": 0,
  "aggs": {
    "older_person": {
      "filter":{
        "range":{
          "age":{
            "from":35
          }
        }
      },
      "aggs":{
         "jobs":{
           "terms": {
        "field":"job.keyword"
      }
      }
    }},
    "all_jobs": {
      "terms": {
        "field":"job.keyword"
        
      }
    }
  }
}
```

#### Post_Filter

- 是对聚合分析后的文档进行再次过滤
- Size 无需设置为 0
- 使用场景: 一条语句，获取聚合信息 + 获取符合条件的文档



Code



```
#Post field. 一条语句，找出所有的job类型。还能找到聚合后符合条件的结果

POST employees/_search
{
  "aggs": {
    "jobs": {
      "terms": {
        "field": "job.keyword"
      }
    }
  },
  "post_filter": {
    "match": {
      "job.keyword": "Dev Manager"
    }
  }
}
```

#### Global

Global，无视 query，对全部文档进行统计



Code



```
# global
POST employees/_search
{
  "size": 0,
  "query": {
    "range": {
      "age": {
        "gte": 35
      }
    }
  },
  "aggs": {
    "jobs": {
      "terms": {
        "field": "job.keyword"
      }
    },
    "all": {
      "global": {}, 
      "aggs": {
        "salary_avg": {
          "avg": {
            "field": "salary"
          }
        }
      }
    }
  }
}
```

### 排序

#### 指定 order， 按照 count 和 key 进行排序

- 默认情况，按照 count 降序排序
- 指定 size，就能返回相应的桶



Code



```
#排序 order
#count and key
POST employees/_search
{
  "size": 0,
  "query": {
    "range": {
      "age": {
        "gte": 20
      }
    }
  },
  "aggs": {
    "jobs": {
      "terms": {
        "field":"job.keyword",
        "order":[
          {"_count":"asc"},
          {"_key":"desc"}
          ]
        
      }
    }
  }
}
```

#### 基于子聚合的值排序

- 基于子聚合的数值进行排序
- 使用子聚合，Aggregation name



Code



```
#排序 order
#count and key
POST employees/_search
{
  "size": 0,
  "aggs": {
    "jobs": {
      "terms": {
        "field":"job.keyword",
        "order":[  {
            "avg_salary":"desc"
          }]
        
        
      },
    "aggs": {
      "avg_salary": {
        "avg": {
          "field":"salary"
        }
      }
    }
    }
  }
}

#排序 order
#count and key
POST employees/_search
{
  "size": 0,
  "aggs": {
    "jobs": {
      "terms": {
        "field":"job.keyword",
        "order":[  {
            "stats_salary.min":"desc"
          }]
        
        
      },
    "aggs": {
      "stats_salary": {
        "stats": {
          "field":"salary"
        }
      }
    }
    }
  }
}
```

## 48 | 聚合的精准度问题

### 分布式系统的近似统计算法

[![image-20210110180551987](https://wangzhangtao.com/img/body/Elasticsearch%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AF%E4%B8%8E%E5%AE%9E%E6%88%98/image-20210110180551987.png)](https://wangzhangtao.com/img/body/Elasticsearch核心技术与实战/image-20210110180551987.png)

image-20210110180551987

Min 聚合分析的执行流程

Min 发出聚合命令 -> 各个节点获取本节点 min value -> Coordination 节点统计最小值 -> 返回索引最小值

在 Terms Aggregation 的返回中有两个特殊的数值

- doc_count_error_upper_bound ： 被遗漏的term 分桶，包含的文档，有可能的最大值
- sum_other_doc_count: 除了返回结果 bucket 的 terms 以外，其他 terms 的文档总数（总数-返回的总数）

Terms 聚合分析的执行流程

max 发出聚合命令 -> 各个节点获取本节点 max value -> Coordination 节点统计最大值 -> 返回索引最大值

### 解决 Terms 不准的问题

Terms 不正确的案例

提升 shard_size 的参数

- Terms 聚合分析不准的原因，数据分散在多个分片上， Coordinating Node 无法获取数据全貌
- 方案 1：当数据量不大时，设置 Primary Shard 为 1；实现准确性
- 方案 2：在分布式数据上，设置 shard_size 参数，提高精确度; 原理：每次从 Shard 上额外多获取数据，提升准确率

打开 show_term_doc_count_error, 测试数据请看最后



Code



```
GET kibana_sample_data_flights/_search
{
  "size": 0,
  "aggs": {
    "weather": {
      "terms": {
        "field":"OriginWeather",
        "size":5,
        "show_term_doc_count_error":true
      }
    }
  }
}


GET my_flights/_search
{
  "size": 0,
  "aggs": {
    "weather": {
      "terms": {
        "field":"OriginWeather",
        "size":1,
        "shard_size": 5,
        "show_term_doc_count_error":true
      }
    }
  }
}
```

shard_size 设定

- 调整 shard size 大小，降低 doc_count_error_upper_bound 来提升准确度
  - 增加整体计算量，提高了准确度，但会降低相应时间
- Shard Size 默认大小设定
  - shard size = size *1.5 +10
  - https://www.elastic.co/guide/en/elasticsearch/reference/7.1/search-aggregations-bucket-terms-aggregation.html#search-aggregations-bucket-terms-aggregation-approximate-counts

### 测试数据



Code



```
DELETE my_flights
PUT my_flights
{
  "settings": {
    "number_of_shards": 20
  },
  "mappings" : {
      "properties" : {
        "AvgTicketPrice" : {
          "type" : "float"
        },
        "Cancelled" : {
          "type" : "boolean"
        },
        "Carrier" : {
          "type" : "keyword"
        },
        "Dest" : {
          "type" : "keyword"
        },
        "DestAirportID" : {
          "type" : "keyword"
        },
        "DestCityName" : {
          "type" : "keyword"
        },
        "DestCountry" : {
          "type" : "keyword"
        },
        "DestLocation" : {
          "type" : "geo_point"
        },
        "DestRegion" : {
          "type" : "keyword"
        },
        "DestWeather" : {
          "type" : "keyword"
        },
        "DistanceKilometers" : {
          "type" : "float"
        },
        "DistanceMiles" : {
          "type" : "float"
        },
        "FlightDelay" : {
          "type" : "boolean"
        },
        "FlightDelayMin" : {
          "type" : "integer"
        },
        "FlightDelayType" : {
          "type" : "keyword"
        },
        "FlightNum" : {
          "type" : "keyword"
        },
        "FlightTimeHour" : {
          "type" : "keyword"
        },
        "FlightTimeMin" : {
          "type" : "float"
        },
        "Origin" : {
          "type" : "keyword"
        },
        "OriginAirportID" : {
          "type" : "keyword"
        },
        "OriginCityName" : {
          "type" : "keyword"
        },
        "OriginCountry" : {
          "type" : "keyword"
        },
        "OriginLocation" : {
          "type" : "geo_point"
        },
        "OriginRegion" : {
          "type" : "keyword"
        },
        "OriginWeather" : {
          "type" : "keyword"
        },
        "dayOfWeek" : {
          "type" : "integer"
        },
        "timestamp" : {
          "type" : "date"
        }
      }
    }
}


POST _reindex
{
  "source": {
    "index": "kibana_sample_data_flights"
  },
  "dest": {
    "index": "my_flights"
  }
}

GET kibana_sample_data_flights/_count
GET my_flights/_count
```

# Scroll-知识插播

```http
#官网地址：
https://www.elastic.co/guide/cn/elasticsearch/guide/current/scroll.html
```

`scroll` 查询 可以用来对 [Elasticsearch](https://so.csdn.net/so/search?q=Elasticsearch&spm=1001.2101.3001.7020) 有效地执行大批量的文档查询，而又不用付出深度分页那种代价。

游标查询允许我们 先做查询初始化，然后再批量地拉取结果。 这有点儿像传统数据库中的 *cursor* 。

游标查询会取某个时间点的快照数据。 查询初始化之后索引上的任何变化会被它忽略。 它通过保存旧的数据文件来实现这个特性，结果就像保留初始化时的索引 *视图* 一样。

深度分页的代价根源是结果集全局排序，如果去掉全局排序的特性的话查询结果的成本就会很低。 游标查询用字段 `_doc` 来排序。 这个指令让 Elasticsearch 仅仅从还有结果的分片返回下一批结果。

启用游标查询可以通过在查询的时候设置参数 `scroll` 的值为我们期望的游标查询的过期时间。 ==游标查询的过期时间会在每次做查询的时候刷新，所以这个时间只需要足够处理当前批的结果就可以了，而不是处理查询结果的所有文档的所需时间==。 这个过期时间的参数很重要，因为保持这个游标查询窗口需要消耗资源，所以我们期望如果不再需要维护这种资源就该早点儿释放掉。 设置这个超时能够让 Elasticsearch 在稍后空闲的时候自动释放这部分资源。

```groovy
GET /old_index/_search?scroll=1m   //保持游标查询窗口一分钟。
{
    "query": { "match_all": {}},
    "sort" : ["_doc"],   //关键字 _doc 是最有效的排序顺序。
    "size":  1000
}
```

这个查询的返回结果包括一个字段 `_scroll_id`， 它是一个base64编码的长字符串 。 现在我们能传递字段 `_scroll_id` 到 `_search/scroll` 查询接口获取下一批结果：

```groovy
GET /_search/scroll
{
    "scroll": "1m",   //注意再次设置游标查询过期时间为一分钟。
    "scroll_id" : "cXVlcnlUaGVuRmV0Y2g7NTsxMDk5NDpkUmpiR2FjOFNhNnlCM1ZDMWpWYnRROzEwOTk1OmRSamJHYWM4U2E2eUIzVkMxalZidFE7MTA5OTM6ZFJqYkdhYzhTYTZ5QjNWQzFqVmJ0UTsxMTE5MDpBVUtwN2lxc1FLZV8yRGVjWlI2QUVBOzEwOTk2OmRSamJHYWM4U2E2eUIzVkMxalZidFE7MDs="
}
```

> 注意游标查询每次返回一个新字段 `_scroll_id`。每次我们做下一次游标查询， 我们必须把前一次查询返回的字段 `_scroll_id` 传递进去。 当没有更多的结果返回的时候，我们就处理完所有匹配的文档了。

这个游标查询返回的下一批结果。 尽管我们指定字段 `size` 的值为1000，我们有可能取到超过这个值数量的文档。 当查询的时候， 字段 `size` 作用于单个分片，所以每个批次实际返回的文档数量最大为 `size * number_of_primary_shards` 。

## 1. scroll-scan 的高效滚动

分页检索即from-size形式，from指的是从哪里开始拿数据，size是结果集中返回的文档个数。from-size的工作原理是：如size=10&from=100，那么Elasticsearch会从每个分片里取出110条数据，然后汇集到一起再排序，取出101~110序号的文档。由此可见，from-size的效率必然不会很高，特别是分页越深，需要排序的数据越多，其效率就越低。 

这时更为有效的方法是使用Scroll-Scan。Scroll是先做一次初始化搜索把所有符合搜索条件的结果缓存起来生成一个快照，然后持续地、批量地从快照里拉取数据直到没有数据剩下。而这时对索引数据的插入、删除、更新都不会影响遍历结果，因此scroll 并不适合用来做实时搜索。Scan是搜索类型，告诉Elasticsearch不用对结果集进行排序，只要分片里还有结果可以返回，就返回一批结果。scroll- scan使用中不能跳页获取结果，必须一页接着一页获取。

为了使用scroll-scan，需要执行一个初始化搜索请求，将search_type设置成scan，并且传递一个scroll参数来告诉 Elasticsearch缓存应该持续多长时间，在缓存持续时间内初始化搜索请求后对索引的修改不会反应到快照中。每次搜索请求后都会返回一个scrollId，是一个 64 位的字符串编码，后续会使用此scrollId来获取数据。scroll时间指的是本次数据处理所需要的时间，如果超过此时间，继续使用该scrollId搜索数据则会报错。在使用scroll-scan时可以指定返回结果集大小，在 scan 的时候，size 作用在每个分片上，所以将会在每批次中得到最大为 size * 主分片数 个文档。

一般来说，你仅仅想要找到结果，不关心顺序。你可以通过组合 scroll 和 scan 来关闭任何打分或者排序，以最高效的方式返回结果。你需要做的就是将 search_type=scan 加入到查询的字符串中：

```groovy
POST /twitter/tweet/_search?scroll=1m&search_type=scan
{
   "query": {
       "match" : {
           "title" : "elasticsearch"
       }
   }
}
# 设置 search_type 为 scan 可以关闭打分，让滚动更加高效。
```

扫描式的滚动请求和标准的滚动请求有四处不同：

1. 不算分，关闭排序。结果会按照在索引中出现的顺序返回。
2. 不支持聚合
3. 初始 search 请求的响应不会在 hits 数组中包含任何结果。第一批结果就会按照第一个 scroll 请求返回。
4. 参数 size 控制了每个分片上而非每个请求的结果数目，所以 size 为 10 的情况下，如果命中了 5 个分片，那么每个 scroll 请求最多会返回 50 个结果。

## 2. 清除 scroll API

搜索上下文当 `scroll` 超时就会自动移除。但是保持 scroll 存活需要代价，如在前一节讲的那样，所以 scrolls 当scroll不再被使用的时候需要被用 `clear-scroll` 显式地清除：

```groovy
DELETE /_search/scroll
{ 
"scroll_id" : ["c2Nhbjs2OzM0NDg1ODpzRlBLc0FXNlNyNm5JWUc1"]
}
```

所有搜索上下文可以通过 `_all` 参数而清除：

```sql
DELETE /_search/scroll/_all
```

`scroll_id` 也可以使用一个查询字符串的参数或者在请求的body中传递。多个scroll ID 可以使用逗号分隔传入：

```sql
DELETE /_search/scroll/DXF1ZXJ5QW5kRmV0Y2gBAAAAAAAAAD4WYm9laVYtZndUQlNsdDcwakFMNjU1QQ==,DnF1ZXJ5VGhlbkZldGNoBQAAAAAAAAABFmtSWWRRWUJrU2o2ZExpSGJCVmQxYUEAAAAAAAAAAxZrUllkUVlCa1NqNmRMaUhiQlZkMWFBAAAAAAAAAAIWa1JZZFFZQmtTajZkTGlIYkJWZDFhQQAAAAAAAAAFFmtSWWRRWUJrU2o2ZExpSGJCVmQxYUEAAAAAAAAABBZrUllkUVlCa1NqNmRMaUhiQlZkMWFB
```

## 3. Sliced Scroll 

对于返回大量文档的滚动查询，可以将滚动分割为多个切片，可以单独使用：

```haskell
POST ip:port/index/type/_search?scroll=1m
{
   "query": { "match_all": {}},
    "slice": {
        "id": 0,
        "max": 5
    }   
}
```

# 实例分享

## 1. scroll滚动查询

```java

/**
 * 滚动查询数据
 * @param indexName
 * @param utime
 */
public List<String> scrollSearchAll(String indexName, String utime) throws IOException{
 
    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
    boolQueryBuilder.must(QueryBuilders.rangeQuery("utime").lt(utime).gt("946656000"));//946656000为2000-01-01 00:00:00
 
    //builder
    SearchSourceBuilder builder = new SearchSourceBuilder()
            .query(boolQueryBuilder)
            .size(500);
 
    // 构建SearchRequest
    SearchRequest searchRequest = new SearchRequest();
    searchRequest.indices(indexName);
    searchRequest.source(builder);
 
    Scroll scroll = new Scroll(new TimeValue(600000));
    searchRequest.scroll(scroll);
 
    SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
 
    String scrollId = searchResponse.getScrollId();
    SearchHit[] hits = searchResponse.getHits().getHits();
 
    List<String> resultSearchHit = new ArrayList<>();
 
    while (ArrayUtils.isNotEmpty(hits)) {
        
        for (SearchHit hit : hits) {
            log.info("准备删除的数据hit:{}", hit);
            resultSearchHit.add(hit.getId());
        }
 
        // 再次发送请求,并使用上次搜索结果的ScrollId
        SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
        searchScrollRequest.scroll(scroll);
        SearchResponse searchScrollResponse = restHighLevelClient.searchScroll(searchScrollRequest);
 
        scrollId = searchScrollResponse.getScrollId();
        hits = searchScrollResponse.getHits().getHits();
    }
    // 及时清除es快照，释放资源
    ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
    clearScrollRequest.addScrollId(scrollId);
    restHighLevelClient.clearScroll(clearScrollRequest);
 
    return resultSearchHit;
}
```



# logstash-es集群迁移-知识插播

## 1.下载logstash

```http
https://www.elastic.co/cn/downloads/past-releases#logstash
```

## 2.创建配置文件qianyi.conf内容如下

```shell
#迁出方
input{
	elasticsearch{
		index => "crm_clue"
		hosts => ["http://elasticsearch-7.neibu.koolearn.com"]
		user => 'manageapp'
		password => 'XM5zIcuWlMky'
		size => 1000
		scroll => '1m'
    docinfo => true
    query =>'{"query":{"term":{"dm_id":{"value": "208866"}}}}'
	}
}
#迁入方
output{
	elasticsearch{
		hosts => ["http://172.16.95.138:9200"]
		index => "crm_clue"
		document_id => '%{[@metadata][_id]}'
	}
}
```

参数说明：

| 参数           | 说明                                                         |
| :------------- | :----------------------------------------------------------- |
| hosts          | ES服务的访问地址。input中为http://<阿里云ES公网地址>:<端口>:output中为http://腾讯云ES实例 |
| user           | 访问ES服务的用户名                                           |
| password       | 访问ES服务的密码                                             |
| index          | 指定同步索引名，通配符*代表所有索引                          |
| docinfo        | 设置为true，将会提取ES文档的元信息，例如index、type和id      |
| docinfo_fields | 指定文档元信息，默认不带routing元信息                        |



## 3.运行如下命令

```shell
/Users/kevinlee/Desktop/logstash/bin/logstash -f /Users/kevinlee/Desktop/logstash/qianyi.conf  --config.reload.automatic --http.port 23153 --path.data /Users/kevinlee/Desktop/logstash/data/23153 --path.logs /Users/kevinlee/Desktop/logstash/logs/23153
```

