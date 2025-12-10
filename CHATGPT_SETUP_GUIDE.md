# ChatGPT API 集成指南 - BadgerMate 应用

## 📋 项目概述
本指南将帮助你在 BadgerMate Android 应用中集成 ChatGPT API，生成基于用户身高/体重的个性化菜单推荐。

---

## 🔧 第一步：获取 OpenAI API Key

### 1. 访问 OpenAI 官网
- 打开 https://platform.openai.com
- 点击右上角 "Sign Up" 或 "Log In"

### 2. 创建 API Key
- 登录后，点击左侧菜单 "API keys"
- 点击 "Create new secret key"
- 复制生成的 API Key（保存在安全的地方）

### 3. 设置使用配额（可选但推荐）
- 在 "Billing" 选项中设置使用限制
- 避免意外费用

**注意**：OpenAI API 是收费的，每个请求都会产生费用。建议查看当前的 token 价格。

---

## 🔐 第二步：在项目中安全地存储 API Key

### 选项 1：使用 local.properties（开发环境）
> ⚠️ **警告**：不要将 local.properties 上传到 Git！

1. 打开 `local.properties` 文件
2. 添加以下行：
```properties
OPENAI_API_KEY=sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

3. 更新 `build.gradle.kts` 来读取这个值：
```kotlin
android {
    defaultConfig {
        buildConfigField(
            "String",
            "OPENAI_API_KEY",
            "\"${project.findProperty("OPENAI_API_KEY") ?: ""}\""
        )
    }
}
```

4. 在 `DiningViewModel.kt` 中使用：
```kotlin
private val openAIApiKey: String
    get() = BuildConfig.OPENAI_API_KEY
```

### 选项 2：使用 Firebase Remote Config（生产环境）
1. 在 Firebase Console 中设置远程配置
2. 从应用中动态获取 API Key
3. 这样可以不重新构建应用就更新 Key

---

## 🛠️ 第三步：更新 DiningViewModel

找到文件 `app/src/main/java/com/cs407/badgermate/ui/dining/DiningViewModel.kt`

将以下这一行：
```kotlin
private val openAIApiKey: String
    get() = "" // TODO: 设置你的 OpenAI API Key
```

改为：
```kotlin
private val openAIApiKey: String
    get() = BuildConfig.OPENAI_API_KEY
```

记得在顶部导入：
```kotlin
import com.cs407.badgermate.BuildConfig
```

---

## 📋 第四步：验证集成

### 1. 构建应用
```bash
./gradlew assembleDebug
```

### 2. 在真机或模拟器上测试
- 打开应用
- 进入 Dining Hall 页面
- 确保 "Dining Hall #1" 等内容显示
- 观察 AI Personalized Recommendation 部分加载

---

## 🚀 第五步：工作流程详解

### 用户流程：
```
用户打开 Dining 页面
    ↓
DiningFragment 初始化 ProfileViewModel
    ↓
监听 Profile 数据变化
    ↓
调用 diningViewModel.generateMenuRecommendation(userProfile)
    ↓
ViewModel 准备数据：
  - 用户身高/体重 → 计算 BMI
  - 用户目标体重 → 计算需求
  - 可用菜单数据 → 发送给 ChatGPT
    ↓
MenuRecommendationRepository 调用 OpenAI API
    ↓
ChatGPT 生成个性化推荐
    ↓
结果显示在 UI 中
```

### 代码交互流程：
```
DiningFragment.kt
  ├─ 初始化 DiningViewModel
  ├─ 初始化 ProfileViewModel
  └─ 观察 profile 变化
     └─ 调用 diningViewModel.generateMenuRecommendation(profile)

DiningViewModel.kt
  └─ generateMenuRecommendation() 方法
     ├─ 验证 API Key 和 profile
     ├─ 设置加载状态 (isLoadingRecommendation = true)
     └─ 使用 viewModelScope 发起异步请求
        └─ MenuRecommendationRepository

MenuRecommendationRepository.kt
  └─ generateMenuRecommendation() 方法
     ├─ 计算 BMI：(weight * 703) / (height² inches)
     ├─ 构造 SystemPrompt（告诉 ChatGPT 它的角色）
     ├─ 构造 UserPrompt（用户的详细信息和菜单）
     └─ 调用 OpenAIApiService.generateMenuRecommendation()

OpenAIApiService.kt (Retrofit)
  └─ POST 请求到 OpenAI API
     └─ 返回 ChatCompletionResponse

响应回 ViewModel
  └─ 更新 UI State 中的 aiRecommendation
     └─ DiningScreen 自动重新绘制显示结果
```

---

## 📊 数据流

### 1. **用户信息** (ProfileEntity)
```kotlin
data class ProfileEntity(
    val heightFeet: String,      // 例如: "5"
    val heightInches: String,    // 例如: "10"
    val weight: String,          // 例如: "180"
    val gender: String,          // 例如: "Male"
    val targetWeight: String     // 例如: "170"
)
```

### 2. **菜单数据** (Dish)
```kotlin
data class Dish(
    val name: String,           // "Grilled Salmon"
    val tags: List<String>,     // ["Omega 3", "High Protein"]
    val calories: Int           // 420
)
```

### 3. **ChatGPT 请求**
系统会自动构造以下信息发送给 ChatGPT：
- 用户的身高、体重、性别、目标体重
- 计算的 BMI 值
- 所有可用的菜品及其卡路里和标签

### 4. **ChatGPT 响应**
ChatGPT 会返回类似以下内容：
```
Based on your profile:
- Height: 5'10"
- Weight: 180 lbs
- Goal: Lose 10 lbs

Recommendations:

1. Grilled Salmon (420 cal)
   - High in Omega-3, supports heart health
   - Excellent protein source for muscle maintenance
   - Pairs well with light sides

2. Caesar Salad (190 cal)
   - Low calorie, filling with greens
   - Good for cutting calories while maintaining nutrition
   
...
```

---

## 🐛 常见问题排查

### 问题 1：API Key 不被识别
**症状**：看到 "OpenAI API Key not configured" 错误

**解决方案**：
1. 检查 `local.properties` 中的 API Key 是否正确
2. 重新构建项目：`./gradlew clean build`
3. 确保没有输入空格或特殊字符

### 问题 2：无法连接到 OpenAI API
**症状**：看到网络错误

**检查清单**：
- 确认设备有互联网连接
- 检查防火墙设置
- 验证 API Key 是否有效（登录 OpenAI 官网检查）

### 问题 3：用户信息为空
**症状**：AI 推荐显示 "User profile not available"

**解决方案**：
1. 确保用户已在 Profile 页面填写身高和体重
2. 确认数据已保存到 Firebase
3. 重新打开应用

### 问题 4：收到 401 Unauthorized 错误
**原因**：API Key 已过期或无效

**解决方案**：
1. 在 OpenAI 官网重新生成 API Key
2. 更新 local.properties
3. 重新构建应用

---

## 💰 成本优化

### 1. 使用更廉价的模型
当前使用 `gpt-3.5-turbo`。如果需要降成本，可以在 `ChatCompletionRequest` 中改为：
```kotlin
data class ChatCompletionRequest(
    val model: String = "gpt-3.5-turbo",  // 最便宜的模型
    // ...
)
```

### 2. 缓存推荐结果
避免每次都重新生成：
```kotlin
private val recommendationCache = mutableMapOf<String, String>()

fun generateMenuRecommendation(userProfile: ProfileEntity?) {
    val cacheKey = "${userProfile?.uid}_${System.currentTimeMillis() / 86400000}"  // 每天一个 key
    
    if (recommendationCache.containsKey(cacheKey)) {
        _uiState.value = _uiState.value.copy(
            aiRecommendation = recommendationCache[cacheKey]!!,
            isLoadingRecommendation = false
        )
        return
    }
    
    // ... 然后正常调用 API
    // 获取结果后：
    recommendationCache[cacheKey] = recommendation
}
```

### 3. 限制调用频率
```kotlin
private var lastRecommendationTime = 0L

fun generateMenuRecommendation(userProfile: ProfileEntity?) {
    val now = System.currentTimeMillis()
    if (now - lastRecommendationTime < 60000) {  // 防止 1 分钟内重复调用
        return
    }
    
    lastRecommendationTime = now
    // ... 继续调用 API
}
```

---

## 🔒 安全最佳实践

### ✅ 要做：
- ✅ 使用 BuildConfig 或 Firebase Remote Config 存储 API Key
- ✅ 在 .gitignore 中添加 local.properties
- ✅ 定期更换 API Key
- ✅ 监控 API 使用情况
- ✅ 对用户输入进行验证

### ❌ 不要：
- ❌ 在源代码中硬编码 API Key
- ❌ 将 local.properties 提交到 Git
- ❌ 在客户端直接公开 API Key
- ❌ 忘记设置使用限额

---

## 🎯 完整检查清单

- [ ] 获得 OpenAI API Key
- [ ] 添加 OPENAI_API_KEY 到 local.properties
- [ ] 更新 build.gradle.kts 来包含 API Key
- [ ] 更新 DiningViewModel 使用 BuildConfig.OPENAI_API_KEY
- [ ] 构建并测试应用
- [ ] 验证 Dining 页面显示 AI 推荐
- [ ] 在 Profile 页面完整填写用户信息
- [ ] 测试生成推荐的完整流程
- [ ] 检查 logcat 日志是否有错误

---

## 📞 技术支持

如遇到问题，可以：
1. 查看 Android Logcat 中的详细错误信息
2. 检查 OpenAI API 状态页面：https://status.openai.com
3. 查看 OpenAI 官方文档：https://platform.openai.com/docs

---

## 📝 更新历史

- **2025-12-09**：初始版本创建
  - 集成 OpenAI ChatGPT API
  - 实现基于用户身高/体重的个性化菜单推荐
  - 完整的错误处理和加载状态

---

祝好运！🚀
