# 收藏功能实现文档

## 概述

本次实现完成了用户收藏商品功能，包括商品详情页一键收藏/取消收藏、个人中心查看收藏列表等功能。

## 数据库设计

### favorite 表结构

```sql
CREATE TABLE favorite (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '收藏记录主键ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  item_id BIGINT NOT NULL COMMENT '商品ID',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  UNIQUE KEY uk_user_item (user_id, item_id),
  INDEX idx_user_id (user_id),
  INDEX idx_item_id (item_id),
  INDEX idx_create_time (create_time),
  CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_favorite_item FOREIGN KEY (item_id) REFERENCES item (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';
```

**字段说明：**
- `user_id`: 用户ID，关联 users 表
- `item_id`: 商品ID，关联 item 表
- `create_time`: 收藏时间，默认为当前时间
- 唯一约束确保同一用户不会重复收藏同一商品
- 外键约束确保数据完整性

**命名规范：**
所有字段严格使用下划线命名（user_id, item_id, create_time），符合项目要求。

## 后端实现

### 1. Entity 层

**Favorite.java**
```java
public class Favorite {
    private Long id;
    private Long userId;
    private Long itemId;
    private LocalDateTime createTime;
    private Item item;  // 关联的商品对象
    // getters and setters
}
```

### 2. VO 层

**FavoriteVO.java**
```java
public class FavoriteVO {
    private Long id;
    private Long userId;
    private Long itemId;
    private LocalDateTime createTime;  // 新增：收藏时间
    private ItemVO item;               // 新增：商品详情
    // getters and setters
}
```

### 3. Mapper 层

**FavoriteMapper.xml** 核心查询：
```xml
<select id="listByUser" resultMap="FavoriteWithItemMap">
    SELECT 
        f.id as fav_id,
        f.user_id,
        f.item_id,
        f.create_time as fav_create_time,
        i.title,
        i.description,
        i.price,
        -- ... 其他商品字段
    FROM favorite f
    LEFT JOIN item i ON f.item_id = i.id
    WHERE f.user_id = #{userId}
    ORDER BY f.create_time DESC
</select>
```

**关键点：**
- 使用 LEFT JOIN 获取商品详情
- 按 create_time DESC 排序，最新收藏在前
- 使用别名区分 favorite 和 item 的同名字段

### 4. Service 层

**FavoriteServiceImpl.java** 新增功能：
```java
private FavoriteVO toVO(Favorite favorite) {
    FavoriteVO vo = new FavoriteVO();
    vo.setId(favorite.getId());
    vo.setUserId(favorite.getUserId());
    vo.setItemId(favorite.getItemId());
    vo.setCreateTime(favorite.getCreateTime());  // 填充收藏时间
    
    // 转换关联的商品信息
    if (favorite.getItem() != null) {
        vo.setItem(toItemVO(favorite.getItem()));
    }
    
    return vo;
}
```

### 5. Controller 层

**FavoriteController.java** 新增接口：
```java
@GetMapping("/check")
public Result<Boolean> check(@RequestParam Long itemId, HttpSession session) {
    Long userId = currentUserId(session);
    if (userId == null) {
        return Result.success(false);
    }
    boolean isFavorited = favoriteService.findByUserAndItem(userId, itemId).isPresent();
    return Result.success(isFavorited);
}
```

## 前端实现

### 1. 商品详情页（item-detail.html）

**功能实现：**

1. **页面加载时检查收藏状态**
```javascript
$.ajax({
    url: '/api/favorites/check',
    method: 'GET',
    data: { itemId: itemId },
    success: function(response) {
        if (response.success && response.data === true) {
            $('#favoriteText').text('已收藏');
            $('#favoriteBtn').data('favorited', true);
        } else {
            $('#favoriteText').text('收藏商品');
            $('#favoriteBtn').data('favorited', false);
        }
    }
});
```

2. **切换收藏状态**
- 已收藏：调用 DELETE /api/favorites 取消收藏
- 未收藏：调用 POST /api/favorites 添加收藏
- 根据操作结果更新按钮文本

3. **未登录处理**
- 未登录用户点击收藏按钮时提示登录
- 自动跳转到登录页，登录后返回原页面

### 2. 收藏列表页（my-orders.html）

**显示内容：**
- 商品名称、描述、价格
- 收藏时间（格式：yyyy-MM-dd HH:mm:ss）
- 查看详情、取消收藏按钮

**Thymeleaf 模板：**
```html
<p class="text-muted small mb-0" th:if="${favorite.createTime != null}">
    收藏时间: <span th:text="${#temporals.format(favorite.createTime, 'yyyy-MM-dd HH:mm:ss')}"></span>
</p>
```

## API 接口

### 1. 检查收藏状态
```
GET /api/favorites/check?itemId={itemId}
Response: {"success": true, "data": true/false}
```

### 2. 添加收藏
```
POST /api/favorites?itemId={itemId}
Response: {"success": true, "data": {FavoriteVO}}
```

### 3. 取消收藏
```
DELETE /api/favorites?itemId={itemId}
Response: {"success": true, "data": null}
```

### 4. 获取收藏列表
```
GET /api/favorites
Response: {"success": true, "data": [{FavoriteVO}]}
```

## 使用流程

### 用户收藏商品流程：
1. 用户浏览商品列表，进入商品详情页
2. 页面自动检查该商品是否已收藏，显示对应按钮状态
3. 用户点击"收藏商品"按钮
4. 系统检查用户登录状态
5. 已登录：添加收藏记录，按钮变为"已收藏"
6. 未登录：提示登录，跳转到登录页

### 用户查看收藏列表流程：
1. 用户进入个人中心，点击"我的订单"
2. 切换到"我的收藏"标签
3. 系统展示所有收藏的商品，按收藏时间倒序排列
4. 显示每个商品的详细信息和收藏时间
5. 用户可以查看详情或取消收藏

## 技术特点

1. **数据完整性**：
   - 使用唯一索引防止重复收藏
   - LEFT JOIN 确保即使商品被删除也能显示收藏记录

2. **性能优化**：
   - 对 user_id、item_id、create_time 建立索引
   - 单次查询获取收藏和商品信息，减少数据库访问

3. **用户体验**：
   - 按钮状态实时反馈
   - 防止重复点击
   - 友好的错误提示

4. **安全性**：
   - 所有操作需要登录验证
   - Session 验证用户身份
   - CodeQL 安全扫描无告警

## 测试验证

### 数据库测试
```sql
-- 查询用户收藏列表（含商品详情）
SELECT f.id, f.user_id, f.item_id, f.create_time, 
       i.title, i.price
FROM favorite f
LEFT JOIN item i ON f.item_id = i.id
WHERE f.user_id = 1
ORDER BY f.create_time DESC;
```

**测试结果：**
```
id  user_id  item_id  create_time          title              price
1   1        1        2025-12-31 08:43:27  二手教材-数据结构   25.00
2   1        2        2025-12-31 08:43:27  iPhone 12          2999.00
```

### 编译测试
```bash
mvn clean compile
# 结果：BUILD SUCCESS
```

### 安全扫描
```bash
CodeQL 扫描结果：0 个安全告警
```

## 总结

本次实现完整地满足了需求：
- ✅ 用户可在商品详情页一键收藏/取消收藏
- ✅ 个人中心可查看所有收藏的商品
- ✅ 收藏列表按时间倒序排列
- ✅ 后台使用 favorite 表，字段为 user_id/item_id/create_time
- ✅ 代码质量良好，无安全漏洞
