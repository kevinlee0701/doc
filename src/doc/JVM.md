#### 什么是双亲委派机制

##### 定义：

当一个类加载器收到了类加载的请求的时候，他不会直接去加载指定的类，而是把这个请求委托给自己的父加载器去加载。 只有父加载器无法加载这个类的时候，才会由当前这个加载器来负责类的加载。

- Java语言系统中支持以下4种类加载器：
  - Bootstrap ClassLoader 启动类加载器
  - Extention ClassLoader 标准扩展类加载器
  - Application ClassLoader 应用类加载器
  - User ClassLoader 用户自定义类加载器

- ![image-20211215171547167](/Users/kevinlee/Library/Application Support/typora-user-images/image-20211215171547167.png)

##### 什么情况下父加载器会无法加载某一个类

Java中提供的这四种类型的加载器，是有各自的职责的：

- Bootstrap ClassLoader ，主要负责加载Java核心类库，%JRE_HOME%\lib下的rt.jar、resources.jar、charsets.jar和class等。
- Extention ClassLoader，主要负责加载目录%JRE_HOME%\lib\ext目录下的jar包和class文件。
- Application ClassLoader ，主要负责加载当前应用的classpath下的所有类
- User ClassLoader ， 用户自定义的类加载器,可加载指定路径的class文件

那么也就是说，一个用户自定义的类，如com.hollis.ClassHollis 是无论如何也不会被Bootstrap和Extention加载器加载的。

##### 为什么需要双亲委派

- 首先，**通过委派的方式，可以避免类的重复加载**，当父加载器已经加载过某一个类时，子加载器就不会再重新加载这个类

- 另外，**通过双亲委派的方式，还保证了安全性**。因为Bootstrap ClassLoader在加载的时候，只会加载JAVA_HOME中的jar包里面的类，如java.lang.Integer，那么这个类是不会被随意替换的，除非有人跑到你的机器上， 破坏你的JDK。那么，就可以避免有人自定义一个有破坏功能的java.lang.Integer被加载。这样可以有效的防止核心Java API被篡改。

##### "父子加载器"之间的关系是继承吗

- **双亲委派模型中，类加载器之间的父子关系一般不会以继承（Inheritance）的关系来实现，而是都使用组合（Composition）关系来复用父加载器的代码的**

  如下为ClassLoader中父加载器的定义：

  ```java
  public abstract class ClassLoader {
      // The parent class loader for delegation
      private final ClassLoader parent;
  }
  ```

##### 双亲委派是怎么实现的

- **实现双亲委派的代码都集中在java.lang.ClassLoader的loadClass()方法之中**：

  ```java
  protected Class<?> loadClass(String name, boolean resolve)
          throws ClassNotFoundException
      {
          synchronized (getClassLoadingLock(name)) {
              // First, check if the class has already been loaded
              Class<?> c = findLoadedClass(name);
              if (c == null) {
                  long t0 = System.nanoTime();
                  try {
                      if (parent != null) {
                          c = parent.loadClass(name, false);
                      } else {
                          c = findBootstrapClassOrNull(name);
                      }
                  } catch (ClassNotFoundException e) {
                      // ClassNotFoundException thrown if class not found
                      // from the non-null parent class loader
                  }
  
                  if (c == null) {
                      // If still not found, then invoke findClass in order
                      // to find the class.
                      long t1 = System.nanoTime();
                      c = findClass(name);
  
                      // this is the defining class loader; record the stats
                      sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                      sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                      sun.misc.PerfCounter.getFindClasses().increment();
                  }
              }
              if (resolve) {
                  resolveClass(c);
              }
              return c;
          }
      }
  ```

   主要就是以下几个步骤:

  - 先检查类是否已经被加载过
  - 若没有加载则调用父加载器的loadClass()方法进行加载
  - 若父加载器为空则默认使用启动类加载器作为父加载器
  - 如果都加载失败，抛出ClassNotFoundException异常后，再调用自己的findClass()方法进行加载【调用应用加载器】

##### 如何主动破坏双亲委派机制

双亲委派过程都是在loadClass方法中实现的，那么**想要破坏这种机制，那么就自定义一个类加载器，重写其中的loadClass方法，使其不进行双亲委派即可**。



##### loadClass（）、findClass（）、defineClass（）区别

- loadClass()
  - 主要进行类加载的方法，默认的双亲委派机制就实现在这个方法中。
- findClass()
  - 根据名称或位置加载.class字节码
- definclass()
  - 把字节码转化为Class

如果我们想定义一个类加载器，但是不想破坏双亲委派模型的时候可以重写findClass方法

```java
 /**
 * @since  1.2
 */
protected Class<?> findClass(String name) throws ClassNotFoundException {
    throw new ClassNotFoundException(name);
}

```

这个方法只抛出了一个异常，没有默认实现。

JDK1.2之后已不再提倡用户直接覆盖loadClass()方法，而是建议把自己的类加载逻辑实现到findClass()方法中。

因为在loadClass()方法的逻辑里，如果父类加载器加载失败，则会调用自己的findClass()方法来完成加载。

**所以，如果你想定义一个自己的类加载器，并且要遵守双亲委派模型，那么可以继承ClassLoader，并且在findClass中实现你自己的加载逻辑即可**



##### 双亲委派被破坏的例子

> **第一种被破坏的情况是在双亲委派出现之前。**

由于双亲委派模型是在JDK1.2之后才被引入的，而在这之前已经有用户自定义类加载器在用了。所以，这些是没有遵守双亲委派原则的。

> **第二种，是JNDI、JDBC等需要加载SPI接口实现类的情况。**

> **第三种是为了实现热插拔热部署工具。为了让代码动态生效而无需重启，实现方式时把模块连同类加载器一起换掉就实现了代码的热替换**。

> **第四种时tomcat等web容器的出现。**

> **第五种时OSGI、Jigsaw等模块化技术的应用。**

##### 为什么JNDI，JDBC等需要破坏双亲委派

我们日常开发中，大多数时候会通过API的方式调用Java提供的那些基础类，这些基础类时被Bootstrap加载的。

但是，调用方式除了API之外，还有一种SPI的方式。

如典型的JDBC服务，我们通常通过以下方式创建数据库连接：

```java
Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql", "root", "1234");
```

在以上代码执行之前，DriverManager会先被类加载器加载，因为java.sql.DriverManager类是位于rt.jar下面的 ，所以他会被根加载器加载。

类加载时，会执行该类的静态方法。其中有一段关键的代码是：

```java
ServiceLoader<Driver> loadedDrivers = ServiceLoader.load(Driver.class);
```

这段代码，会尝试加载classpath下面的所有实现了Driver接口的实现类。

那么，问题就来了。

**DriverManager是被根加载器加载的，那么在加载时遇到以上代码，会尝试加载所有Driver的实现类，但是这些实现类基本都是第三方提供的，根据双亲委派原则，第三方的类不能被根加载器加载。**

那么，怎么解决这个问题呢？

于是，就**在JDBC中通过引入ThreadContextClassLoader（线程上下文加载器，默认情况下是AppClassLoader）的方式破坏了双亲委派原则。**

我们深入到ServiceLoader.load方法就可以看到：

```java
public static <S> ServiceLoader<S> load(Class<S> service) {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    return ServiceLoader.load(service, cl);
}
```

第一行，获取当前线程的线程上下⽂类加载器 AppClassLoader，⽤于加载 classpath 中的具体实现类。

##### 为什么Tomcat要破坏双亲委派

我们知道，Tomcat是web容器，那么一个web容器可能需要部署多个应用程序。

不同的应用程序可能会依赖同一个第三方类库的不同版本，但是不同版本的类库中某一个类的全路径名可能是一样的。

如多个应用都要依赖hollis.jar，但是A应用需要依赖1.0.0版本，但是B应用需要依赖1.0.1版本。这两个版本中都有一个类是com.hollis.Test.class。

**如果采用默认的双亲委派类加载机制，那么是无法加载多个相同的类。**

所以，**Tomcat破坏双亲委派原则，提供隔离的机制，为每个web容器单独提供一个WebAppClassLoader加载器。**

Tomcat的类加载机制：为了实现隔离性，优先加载 Web 应用自己定义的类，所以没有遵照双亲委派的约定，每一个应用自己的类加载器——WebAppClassLoader负责加载本身的目录下的class文件，加载不到时再交给CommonClassLoader加载，这和双亲委派刚好相反。



##### 模块化技术与类加载机制

近几年模块化技术已经很成熟了，在JDK 9中已经应用了模块化的技术。

其实早在JDK 9之前，OSGI这种框架已经是模块化的了，**而OSGI之所以能够实现模块热插拔和模块内部可见性的精准控制都归结于其特殊的类加载机制，加载器之间的关系不再是双亲委派模型的树状结构，而是发展成复杂的网状结构。**

![-w942](https://www.hollischuang.com/wp-content/uploads/2021/01/16102754973998.jpg)

**在JDK中，双亲委派也不是绝对的了。**

在JDK9之前，JVM的基础类以前都是在rt.jar这个包里，这个包也是JRE运行的基石。

这不仅是违反了单一职责原则，同样程序在编译的时候会将很多无用的类也一并打包，造成臃肿。

**在JDK9中，整个JDK都基于模块化进行构建，以前的rt.jar, tool.jar被拆分成数十个模块，编译的时候只编译实际用到的模块，同时各个类加载器各司其职，只加载自己负责的模块。**

```java
Class<?> c = findLoadedClass(cn);
if (c == null) {
    // 找到当前类属于哪个模块
    LoadedModule loadedModule = findLoadedModule(cn);
    if (loadedModule != null) {
        //获取当前模块的类加载器
        BuiltinClassLoader loader = loadedModule.loader();
        //进行类加载
        c = findClassInModuleOrNull(loadedModule, cn);
     } else {
          // 找不到模块信息才会进行双亲委派
            if (parent != null) {
              c = parent.loadClassOrNull(cn);
            }
      }
}

```



#### JVM 内存结构

##### Java 虚拟机的内存空间分为 5 个部分

- 程序计数器
- Java 虚拟机栈
- 本地方法栈
- 堆
- 方法区

![image-20211216110025335](/Users/kevinlee/Library/Application Support/typora-user-images/image-20211216110025335.png)



##### java堆

- 存储对象实例

- 垃圾回收管理的主要区域：主要是在伊甸园区和老年区。

- 细分三个区域

  - 新生区：伊甸园区（Eden），幸存0区（Survivor0，from区），幸存1区（Survivor01，to区），谁空谁是to区

  - 老年区

  - 永久区(jdk8叫元空间)

  - ![image-20211216144210134](/Users/kevinlee/Library/Application Support/typora-user-images/image-20211216144210134.png)

    

- 如果在堆中没有内存完成实例分配，并且堆也无法再扩展时，将会抛出OutOfMemoryError异常。

- 对象分配过程

  - new 的对象先放在 Eden 区，大小有限制。
  - 如果创建新对象时，Eden 空间填满了，就会触发 Minor GC，将 Eden 不再被其他对象引用的对象进行销毁，再加载新的对象放到 Eden 区，特别注意的是 Survivor 区满了是不会触发 Minor GC 的，而是 Eden 空间填满了，Minor GC 才顺便清理 Survivor 区。
  - 经过第一次Minor GC，仍然存活的，将会从 Eden 中移到 Survivor 区。
  - 再次触发垃圾回收，此时上次 Survivor 下来的，放在 Survivor0 区的，如果没有回收，就会放到 Survivor1 区。
  - 再次经历垃圾回收，又会将幸存者重新放回 Survivor0 区，依次类推。
  - 默认是 15 次的循环，超过 15 次，则会将幸存者区幸存下来的转去老年区 jvm 参数设置次数 : ==-XX:MaxTenuringThreshold=N== 进行设置。
  - 频繁在新生区收集，很少在养老区收集，几乎不在永久区/元空间搜集。



##### 方法区

- 各个线程共享的内存区域，`它用于存储已被虚拟机加载的类信息、常量、静态变量、即时编译器编译后的代码等数据`
- 永久代。 方法区中的信息一般需要长期存在，而且它又是堆的逻辑分区，因此用堆的划分方法，把方法区称为“永久代”。
- 内存回收效率低。 方法区中的信息一般需要长期存在，回收一遍之后可能只有少量信息无效。主要回收目标是：对常量池的回收；对类型的卸载

- 根据Java虚拟机规范的规定，当方法区无法满足内存分配需求时，将抛出OutOfMemoryError异常



##### 程序计数器

- 程序计数器（Program Counter Register）是一块较小的内存空间，它的作用可以看做是当前线程所执行的字节码的行号指示器。

- 由于Java虚拟机的多线程是通过线程轮流切换并分配处理器执行时间的方式来实现的，在任何一个确定的时刻，一个处理器（对于多核处理器来说是一个内核）只会执行一条线程中的指令。因此，为了线程切换后能恢复到正确的执行位置，每条线程都需要有一个独立的程序计数器，各条线程之间的计数器互不影响，独立存储，我们称这类内存区域为“线程私有”的内存

- 如果线程正在执行的是一个Java方法，这个计数器记录的是正在执行的虚拟机字节码指令的地址；如果正在执行的是Native方法，这个计数器值则为空（Undefined）



##### Java栈

- Java虚拟机栈（Java Virtual Machine Stacks）也是线程私有的，**它的生命周期与线程相同**。
- 每个方法被执行的时候都会同时创建一个栈帧（Stack Frame）用于存储局部变量表、操作栈、动态链接、方法出口等信息。`每一个方法被调用直至执行完成的过程，就对应着一个栈帧在虚拟机栈中从入栈到出栈的过程`。
- 存储：基本数据类型（boolean、byte、char、short、int、float、long、double），对象引用

- 这个区域规定了两种异常状况：如果线程请求的栈深度大于虚拟机所允许的深度，将抛出***StackOverflowError***异常；如果虚拟机栈可以动态扩展（当前大部分的Java虚拟机都可动态扩展，只不过Java虚拟机规范中也允许固定长度的虚拟机栈），当扩展时无法申请到足够的内存时会抛出***OutOfMemoryError***异常



##### 本地方法栈

本地方法栈是为 JVM 运行 Native 方法准备的空间，由于很多 Native 方法都是用 C 语言实现的，所以它通常又叫 C 栈。它与 Java 虚拟机栈实现的功能类似，只不过本地方法栈是描述本地方法运行过程的内存模型

#### GC题目

> ##### GC算法有哪些

```makefile
引用计数法，复制算法，标记压缩，标记清除法
内存效率：复制算法 > 标记清除法 > 标记压缩(时间复杂度)
内存整齐度：复制算法 = 标记压缩 > 标记清除法
内存利用率：标记压缩 = 标记清除法 > 复制算法

年轻代：存活率低，使用复制算法
老年代：存活率高，使用标记清除（内存碎片不是太多的情况）+标记压缩混合实现
```

##### 引用计数法

引用计数器的实现很简单，对于一个对象A，只要有任何一个对象引用了A，则A的计数器就加1，当引用失效时，引用计数器就减1.只要对象A的引用计数器的值为0，则对象A就不可能再被使用。

引用计数法的问题：
\- 引用和去引用伴随加法和减法，影响性能。
\- 很难处理循环引用。

![image-20211220111701423](/Users/kevinlee/Library/Application Support/typora-user-images/image-20211220111701423.png)







##### 复制算法

复制算法将原有的内存空间分为两块，每次只使用其中一块，在垃圾回收时，将正在使用的内存中的存活对象复制到未使用的内存块中，之后清除正在使用的内存块中的所有对象，交换两个内存的角色，完成垃圾回收。

![image-20211220110955038](/Users/kevinlee/Library/Application Support/typora-user-images/image-20211220110955038.png)

问题：

- 不适用于存活对象较多的场合，如老年代。
- 浪费空间。

##### 标记压缩法

标记压缩算法适合用于存活对象较多的场合，如老年代。它在标记清除算法基础上做了一些优化。和标记清除算法一样，标记压缩算法也首先需要从根节点开始，对所有可达对象做一次标记。但之后，它并不简单的清理未标记的对象，而是将所有的存活对象压缩到内存的一端，之后，清理边界外所有的空间。
![这里写图片描述](https://img-blog.csdn.net/20170831155917874?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvZ2FveWluZ19ibG9ncw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

##### 标记清除法

标记清除算法是现代垃圾回收算法的思想基础，标记-清除算法将垃圾回收分为两个阶段：标记阶段和清除阶段。一种可行的实现是：在标记阶段首先通过根节点，标记所有从根节点开始的可达对象，因此，未被标记的独享就是未被引用的垃圾对象。然后，在清除阶段，清除所有未被标记的对象。

缺点：两次扫描，浪费时间，会产生内存碎片

![image-20211220111309896](/Users/kevinlee/Library/Application Support/typora-user-images/image-20211220111309896.png)



#### JMM（Java memory model）

##### 定义

Java虚拟机规范中定义了Java内存模型（Java Memory Model，JMM），用于屏蔽掉各种硬件和操作系统的内存访问差异，以实现让Java程序在各种平台下都能达到一致的并发效果，JMM规范了Java虚拟机与计算机内存是如何协同工作的：==规定了一个线程如何和何时可以看到由其他线程修改过后的共享变量的值，以及在必须时如何同步的访问共享变量==。

##### 作用：

用于定义数据读写规则，定义了线程工作内存和主内存的抽象关系：线程之间的共享变量存储在主内存中，每个线程都有一个工作内存，工作内存存储在高速缓存中。加入高速缓存带来了一个新的问题：缓存一致性。如果多个缓存共享同一块主内存区域，那么多个缓存的数据可能会不一致，需要一些协议来解决这个问题。解决共享对象问题使用：volatile 关键字，将cpu高速缓存内的值刷新到主内存中。

![image-20211220153406226](/Users/kevinlee/Library/Application Support/typora-user-images/image-20211220153406226.png)

 Java 内存模型定义了 8 个操作来完成主内存和工作内存的交互操作。

![image-20211220153556258](/Users/kevinlee/Library/Application Support/typora-user-images/image-20211220153556258.png)

- read：把一个变量的值从主内存传输到工作内存中

- load：在 read 之后执行，把 read 得到的值放入工作内存的变量副本中

- use：把工作内存中一个变量的值传递给执行引擎

- assign：把一个从执行引擎接收到的值赋给工作内存的变量

- store：把工作内存的一个变量的值传送到主内存中

- write：在 store 之后执行，把 store 得到的值放入主内存的变量中

- lock：作用于主内存的变量

- unlock：作用于主内存的变量







































