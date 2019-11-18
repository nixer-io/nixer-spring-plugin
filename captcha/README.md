# Captcha protection
## Reason
Captcha is simple but powerful way of detecting bots. In simples form you would always challenge user with captcha.  
With Nixer plugin you could dynamically control when captcha should be enabled. By doing that you could li 

## Integration
Currently plugin provides integration only with Google Recaptcha V2.

To integrate captcha application needs to:
 - sign-up for recaptcha
 - displayed it in view
 - verified captcha response on server
 - connect it to Spring Security

### Signup
To start with Recaptcha you need to create API key pair for your application.
Follow official [guide](https://developers.google.com/recaptcha/intro) to sign-up and create keys.

Put your keys in application properties.

```properties
nixer.captcha.recaptcha.verifyUrl=https://www.google.com/recaptcha/api/siteverify
nixer.captcha.recaptcha.key.site=<site_key>
nixer.captcha.recaptcha.key.secret=<secret_key>
```

When performing automated tests for your application your tools will fail due to captcha check. That's what captcha is for.
To solve that problem Recaptcha provides test API keys which makes verification accept any captcha value.   

### Displaying captcha
We assume that mitigation behaviors such as captcha could be dynamically controlled. Because of that displaying captcha should be conditional.
Plugin doesn't provide ready to use view for login.
In sample project found in [link](../samples/example) you could find how to use Thymeleaf templating engine to render captcha conditionally. 
Captcha response code should be submitted as part of login form. 

### Verifying captcha
Verification of captcha is done as part of authentication process. 

Use following properties to control captcha check for login.

```properties
nixer.login.captcha.condition=SESSION_CONTROLLED
nixer.login.captcha.param=g-recaptcha-response
```

### Spring Security setup

Setting up captcha for login requires adding `CaptchaConfigurer` as postprocessor. 
Check [WebSecurityConfig.java](../samples/example/src/main/java/io/nixer/example/WebSecurityConfig.java) for working example.


### Captcha for endpoint
It might be useful to protect not only login but also other endpoints. To do that you could use `CaptchaValidator` in form of standard 
bean validation.  

Here is example how it may look like.

```java
@PostMapping("/userSubscribe")
public String userSubscribe(@ModelAttribute("g-recaptcha-response") @Captcha(action = "user_subscribe", message = "Captcha error") String captcha) {
    ...
}
```

See sample controller in [RecaptchaTestController.java](src/test/java/io/nixer/nixerplugin/captcha/validation/CaptchaValidatorTest.java)

## Detail configuration

### Http client
Verification of captcha is done via Http API. With following properties you could configure http client. 

```properties
nixer.captcha.recaptcha.http.timeout.connect=2000
nixer.captcha.recaptcha.http.timeout.read=2000
nixer.captcha.recaptcha.http.timeout.connectionRequest=2000
nixer.captcha.recaptcha.http.maxConnections=10
```


### Captcha metrics
Micrometer metrics will be reported by default if `meterRegistry` bean is registered. 
Captcha metrics are reported under name `captcha`. 
To disable metrics use standard spring boot metrics filter eg.
```
management.metrics.enable.captcha=false
```

Having multiple endpoints protected with captcha you could use `action` tag to tell metrics apart.
