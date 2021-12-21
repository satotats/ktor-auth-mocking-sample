# ktor-auth-mocking-sample
This project is a sample to mock authentication feature of `ktor`.  

Sometimes we have senarios not to want to use actual authentication features(e.g. in local development, testing, etc).  
By implementing mock authentiation feature and switching to it, we can achieve it.

## Usage(with ktor-server-test)
### Step1. install authentication feature separate from others
```kotlin
fun Application.mainModule() {
    auth() // execute installation of authentication feature
    others()
}
```
https://github.com/satotats/ktor-auth-mocking-sample/blob/master/app/src/main/kotlin/com/satotats/ktor/App.kt#L9  

```kotlin
fun Application.auth() {
    install(Authentication) {
    // ...
```
https://github.com/satotats/ktor-auth-mocking-sample/blob/master/app/src/main/kotlin/com/satotats/ktor/Auth.kt  
Note that the mainModule here is the module which you runs on production.  

### Step2. implement mock authentication feature
```kotlin 
fun Authentication.Configuration.mock(
    name: String? = null,
    configure: MockAuthenticationProvider.Configuration.() -> Unit
) {
    val provider = MockAuthenticationProvider.Configuration(name).apply(configure).build()

    provider.pipeline.intercept(AuthenticationPipeline.RequestAuthentication) { context ->
        val principal = provider.principalProvider(call)

        if (principal == null) {
            call.respond(UnauthorizedResponse())
            context.challenge.complete()
            return@intercept
        }

        context.principal(principal)
    }
    register(provider)
}
```
details: https://github.com/satotats/ktor-auth-mocking-sample/blob/master/app/src/test/kotlin/com/satotats/ktor/TestAuth.kt#L16  

### Step3. define mock authentication module(by installing defined mock feature)
```kotlin
fun Application.testAuth(loginUser: LoginUser? = null) {
    install(Authentication) {
        mock {
            principal { loginUser?.id }
        }
    }
}
```
https://github.com/satotats/ktor-auth-mocking-sample/blob/master/app/src/test/kotlin/com/satotats/ktor/TestAuth.kt#L7  
By passing principal information to `principal{ }` function, it will be able to obtained from `call.principal()` in route.

### Step4. embbed the mock module to module for test
```kotlin
fun Application.testModule(loginUser: LoginUser? = null) {
    testAuth(loginUser)
    others()
}
```
https://github.com/satotats/ktor-auth-mocking-sample/blob/master/app/src/test/kotlin/com/satotats/ktor/TestModule.kt  

### Step5. run test with the module for test
```kotlin
    @Test
    fun `authenticated hello - success`() {
        withTestApplication({ testModule(loginUser = TestUser.Bezos) }) {
            handleRequest(HttpMethod.Get, "/hello") { }
                .apply {
                    assertEquals(HttpStatusCode.OK, response.status())
                    assertEquals("Hello, ${TestUser.Bezos.id.value}", response.content)

                    // "Hello, amzn001"
                    println(response.content)
                }
        }
    }
```
https://github.com/satotats/ktor-auth-mocking-sample/blob/master/app/src/test/kotlin/com/satotats/ktor/AppTest.kt#L11  
Heigh-ho!! Now you can use mocked login, without writing any if-conditions in codes for production!  
Enjoy your clean codes!
