## 说明

本 Demo 对使用 Retrofit 的项目通过 R8 和 Proguard 优化代码的结果进行测试。

### 结论

通过 R8 混淆代码时会自动应用 AAR 依赖包中的 proguard rules，而使用 Proguard 则不会。
除非将对应依赖的混淆配置写入 proguard-rules.pro。

使用 Proguard 混淆代码时，如果没有将 retrofit 相关库的混淆配置写入 proguard-rules.pro 
文件中。则最终的请求可能会缺失参数。比如实际的请求为：

```text
https://api.github.com/orgs/vuejs/members?role=admin&per_page=100
```

最终可能请求为：

```text
https://api.github.com/orgs/vuejs/members?
```

类似以上行为，但不完全确定。
