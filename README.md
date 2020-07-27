# 客户端

直接运行程序即可，无需其他配置

- 将升级包传送给本程序，程序收升级包，并入表记录对应的版本和存放的路径（未实现）
- 收到升级到指定版本指令后，查表获取对应版本的文件路径，将压缩包解压并替换当前允许的程序（部分实现）
- 单独开一个线程调用重启脚本即可（脚本路径`bin/`，未实现）

## 数据库脚本

> 路径:src\main\resources\db\migration\V1_0001__create_table_version_record.sql

```sql
## 数据库为 H2 文件数据库，运行后会在项目 db/ 目录下创建数据库文件
CREATE TABLE `VERSION_RECORD` (
  `VERSION` varchar(32) primary key, # 主键，版本号
  `PATH` TEXT, # 升级包路径
  `OPERATOR` varchar(32), // 操作员
  `UPDATED_AT` varchar(32), // 更新时间
  `SUCCESS` varchar(1) // 是否成功
) ;

```
## 升级接口

> 如升级到1.0版本，调用的URL如下

- `http://localhost:8080/update?version=v1.0`

