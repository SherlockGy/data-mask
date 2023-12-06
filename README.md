# GeneralMaskUtil 使用指南

`GeneralMaskUtil` 是一个用于处理敏感信息掩码的工具类。它可以接收任何对象，并对其所有字段进行检查。如果字段是敏感字段，则将其替换为掩码。此外，它还能处理 JSON 字符串中的敏感字段。

## 功能

- 对任何对象的所有字段进行敏感信息掩码处理
- 对 JSON 字符串中的敏感字段进行掩码处理
- 从配置文件中读取敏感字段
- 支持敏感字段的配置

## 配置方法

敏感字段的配置存储在 `data_mask.properties` 文件中。每一行代表一个敏感字段类型，格式如下：

```
TypeName=alias1,alias2,alias3
```

其中，`TypeName` 是敏感字段的类型名称，`alias1,alias2,alias3` 是该类型的别名，用逗号分隔。

例如，如果你想配置姓名（Name）和电话号码（Phone）为敏感字段，你可以在 `data_mask.properties` 文件中添加以下内容：

```
Name=name
Phone=mobileNo,alterPhoneNo,mobile,identifier
```

在这个例子中，`name`、`mobileNo`、`alterPhoneNo`、`mobile` 和 `identifier` 都会被视为敏感字段，并在处理时被替换为掩码。

## 使用方法

在输出日志时，你可以使用 `GeneralMaskUtil.mask(obj)` 方法来处理对象，例如：

```java
SomeClass obj = new SomeClass();
// ... 设置 obj 的值 ...
logger.info("Object: {}", GeneralMaskUtil.mask(obj));
```

或者，你也可以使用 `GeneralMaskUtil.maskJsonString(json)` 方法来处理 JSON 字符串，例如：

```java
String json = "{\"mobileNo\":\"13115681111\",\"msgCode\":\"XFD_2022\",\"paramJson\":\"{\\\"periodAmount\\\":\\\"37.72\\\",\\\"name\\\":\\\"曹小明\\\"}\"}";
logger.info("JSON: {}", GeneralMaskUtil.maskJsonString(json));
```

这样，日志中的敏感信息就会被掩码处理。