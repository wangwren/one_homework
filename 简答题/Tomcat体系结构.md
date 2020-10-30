# Tomcat体系结构

## 整体架构图

![整体架构图](https://imagebed-1259286100.cos.ap-beijing.myqcloud.com/img/tomcatzhengti.png)

- 一个Servlet主要做下面三件事情：
  - 创建并填充`Request`对象，包括：URI、参数、method、请求头信息、请求体信息等。
  - 创建`Response`对象。
  - 执行业务逻辑，将结果通过`Response`的输出流输出到客户端

Servlet没有`main`方法，所以，如果要执行，则需要在一个*容器*里面才能执行，这个容器就是为了支持Servlet的功能而存在，Tomcat其实就是一个Servlet容器的实现。



从上图可以看出，最核心的两个组件：连接器（Connector）和容器（Container）。

- `Server`表示服务器，提供了一种优雅的方式来启动和停止整个系统，不必单独启停连接器和容器

- `Service`表示服务，`Server`可以运行多个服务。比如一个Tomcat里面可运行订单服务、支付服务、用户服务等等。

- 每个`Service`可包含`多个Connector`和`一个Container`。因为每个服务允许同时支持多种协议，但是每种协议最终执行的Servlet却是相同的。

- `Connector`表示连接器，比如一个服务可以同时支持AJP协议、Http协议和Https协议，每种协议可使用一种连接器来支持。

- `Container`表示容器，可以看做Servlet容器。

  - `Engine`引擎
  - `Host`主机
  - `Context`上下文
  - `Wrapper`包装器

- Service服务之下还有各种支撑组件。

  - `Manager`管理器，用于管理会话session。
  - `Logger`日志器，用于管理日志。
  - `Loader`加载器，和类加载有关，只会开放给Context所使用。
  - `Pipeline`管道组件，配合Valve实现过滤器功能。
  - `Valve`阀门组件，配合Pipeline实现过滤器功能。
  - `Realm`认证授权组件。

  

  ## Tomcat架构3个方面

  ### 基于组件的架构

  我们知道了组成Tomcat的是各种各样的组件，每个组件各司其职，组件与组件之间有明确的职责划分，同时组件与组件之间又通过一定的联系相互通信。Tomcat整体就是一个个组件的堆砌。

  ### 基于JMX

  JMX（Java Management Extensions，即Java管理扩展）是一个为应用程序、设备、系统等植入管理功能的框架。JMX可以跨越一系列异构操作系统平台、系统体系结构和网络传输协议，灵活的开发无缝集成的系统、网络和服务管理应用。

  ### 基于生命周期

  Tomcat的源码中，会发现绝大多数组件实现了`Lifecycle`接口，这就是所说的*基于生命周期*。生命周期的各个阶段的触发又是*基于事件*的方式。

  