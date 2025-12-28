# 商品状态字段重构说明

## 概述
本次重构将所有与商品状态相关的字段从 `itemStatus` 统一更名为 `status`，以保持与数据库字段的一致性。

## 变更内容

### 1. Java 类字段重命名
- **Entity (Item.java)**: `itemStatus` → `status`
- **DTO (ItemDTO.java)**: `itemStatus` → `status`
- **VO (ItemVO.java)**: `itemStatus` → `status`
- 所有对应的 getter/setter 方法已同步更新

### 2. 数据库映射更新
- **ItemMapper.xml**: 列映射从 `item_status` 更改为 `status`
- 需要运行数据库迁移脚本 `migration_item_status.sql` 将数据库列名从 `item_status` 重命名为 `status`

### 3. 新增功能 - 商品上下架接口
添加了专用的商品状态切换接口：
- **端点**: `PUT /api/items/{id}/status`
- **功能**: 支持卖家本人切换商品的上架(status=1)和下架(status=2)状态
- **请求体**: `{ "status": 1 }` 或 `{ "status": 2 }`
- **权限**: 仅商品所有者可以修改

### 4. Service 层新增方法
- **ItemService.updateStatus(Long id, Integer status, Long ownerId)**
  - 参数验证: status 必须为 1(上架) 或 2(下架)
  - 权限检查: 仅允许商品所有者更新状态

### 5. Mapper 层新增方法
- **ItemMapper.updateStatus(Long id, Integer status)**
  - SQL: 更新商品状态并自动更新 update_time 字段

### 6. 前端更新
- **my-orders.html**: 
  - 在"我的发布"标签页中为每个商品添加了"上架/下架"按钮
  - 按钮文本动态显示当前状态的相反操作（上架状态显示"下架"，下架状态显示"上架"）
  - 显示商品当前状态的徽章（上架/下架/已售出）
  - AJAX 处理状态切换请求
  
- **item-edit.html**: 表单字段 ID 和 name 从 `itemStatus` 更新为 `status`

- **index.html**: 商品列表中的状态徽章条件从 `item.itemStatus` 更新为 `item.status`

### 7. ItemVO 字段扩充
为支持前端显示需求，在 ItemVO 中添加了以下字段：
- `contactWay`: 联系方式
- `itemLocation`: 商品位置
- `imageUrls`: 图片 URL

## 状态值说明
- `1`: 上架（商品在售）
- `2`: 下架（暂时不在售）
- `3`: 已售出（已完成交易）

## 数据库迁移
在部署代码更改后，需要运行以下 SQL 脚本：
```sql
ALTER TABLE item CHANGE COLUMN item_status status INT COMMENT '商品状态: 1-上架, 2-下架, 3-已售出';
```

## API 使用示例

### 切换商品状态
```bash
# 下架商品
curl -X PUT http://localhost:8080/api/items/123/status \
  -H "Content-Type: application/json" \
  -d '{"status": 2}'

# 上架商品
curl -X PUT http://localhost:8080/api/items/123/status \
  -H "Content-Type: application/json" \
  -d '{"status": 1}'
```

## 兼容性说明
- 此更改需要同时更新代码和数据库
- 建议先部署代码，然后立即执行数据库迁移脚本
- 旧的 `item_status` 列将被重命名为 `status`
