
## 构建

环境要求：
- java (>1.8)
- clj ([安装clj工具](https://clojure.org/guides/getting_started))
- node & yarn

```bash
# in the project root directory
yarn install
yarn build:node
# 编译后的js文件在out/xmi_parser/main.js
# 创建数据存储文件
mkdir temp && touch UML_metamodel.xlsx
# 执行
node out/xmi_parser/main.js resources/UML20161101.xmi temp/UML_metamodel.xlsx

```
