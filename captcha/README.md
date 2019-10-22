# Captcha protection
## Reason
Captcha is simple but powerful way of detecting bots. In simples form you would always display captcha which might not be something that you want.  
With Nixer plugin you could dynamically control when captcha should be enabled. 
 
## Integration
Currently plugin provides integration only with Google Recaptcha V2.

### Configuration
To integrate captcha into your application you need to create API key pair.
Follow official [guide](https://developers.google.com/recaptcha/intro) to sign-up for captcha and create keys.

Put your keys in application properties for below actions.

```properties
recaptcha.key.site=<site_key>
recaptcha.key.secret=<secret_key>
```

To integrate captcha application needs to:
 - displayed it in view
 - verified captcha response on server
 
### Displaying captcha
We assume that mitigation behaviors such as captcha could be dynamically controlled. Because of that displaying captcha should be conditional.
Plugin doesn't provide ready to use view for login.
In sample project found in `/samples/example` you could find how to use Thymeleaf templating engine to render captcha conditionally. 
Captcha response code should be submitted as part of login form. 

### Verifying captcha
Verification of captcha is done as part of authentication process. 

Use following properties to control captcha check for login.

```properties
nixer.login.captcha.enabled=true
nixer.login.captcha.condition=AUTOMATIC
nixer.login.captcha.param=g-recaptcha-response
```

### Http client
Verification of captcha is done via Http API. With following properties you could configure http client. 

```properties
recaptcha.verifyurl=https://www.google.com/recaptcha/api/siteverify
recaptcha.http.timeout.connect=2000
recaptcha.http.timeout.read=2000
recaptcha.http.timeout.connection-request=2000
recaptcha.http.max-connections=10
```

### Adding captcha protection
It might be useful to protect not only login but also other endpoints. To do that you could use `CaptchaValidator` in form of standard 
bean validation.  

Here is example how it may look like.

```java
@PostMapping("/userSubscribe")
public String userSubscribe(@ModelAttribute("g-recaptcha-response") @Captcha(action = "user_subscribe", message = "Captcha error") String captcha) {
    ...
}
```

See sample controller in `RecaptchaTestController.java`

### Captcha metrics
Use below properties to control reporting metrics for captcha. 

```properties
recaptcha.metrics.enabled=true
```

Captcha metrics are reported under name `recaptcha`. 

Having multiple endpoints protected with captcha you could use `action` tag to tell metrics apart.