## Nixer spring-plugin by Crossword Cybersecurity

A plugin for [Spring](https://github.com/spring-projects/spring-framework) utilising Nixer's protection technologies by [Crossword Cybersecurity](https://www.crosswordcybersecurity.com/). The plugin provides protection against credential stuffing attacks to your Spring web application. 

## Credential Stuffing attack

Credential Stuffing is a peculiar attack on web applications. Technically it is a very basic and easy to understand attack closely related to brute force techniques, yet it is frighteningly effective. 
 
To learn more about credential stuffing take a look at [OWASP definition](https://www.owasp.org/index.php/Credential_stuffing) or our [introductory article into the subject](https://medium.com/@jbron/credential-stuffing-how-its-done-and-what-to-do-with-it-57ad66302ce2).

## Credential Stuffing protection as a Spring library - why?

The motivation for releasing this open-source plugin is to give more insight and control to the developers of the web applications. Majority of available protection software runs as an external piece complicating the architecture and introducing yet another point of failure into the system. In addition, the task of detecting credential stuffing often requires statistical learning or machine learning which results in a black-box software that can't explain its decisions. Also such third-party servers require access to the HTTP traffic, which creates data privacy and security concerns. Administrators are usually reluctant to adopt that kind of black-box protections. It is always not easy to propagate such decisions within the organization. 
   
Apart from privacy and infrastructure considerations, there are also data-science related advantages of such approach. External software needs to read a lot of things directly from HTTP traffic. The process of feature engineering and data augmentation is harder and usually results in complicated algorithms. By moving heuristics directly into the application, we are no longer forced to decode HTTP traffic and we have access to much broader application context. This is especially powerful in Spring, where a lot of configuration parameters about the application are available internally. By leverage Spring's well-though architecture, it is possible to create a library that provides control and ease of integration.

## Features and concepts


### HTTP filters
We use Java Servlet filters for injecting additional functionality into the HTTP pipeline, just like Spring Security and other Spring modules. Every major feature of the plugin will have a dedicated filter which allows fine-grained control over particular features. 

We introduce MetadataFilter concept, in which we use filters to enrich the HTTP request on the pipeline with additional data. When request moves though the filters, more and more information is attached to it. Then, based on that information, a decision about the request is made and BehaviorExecutionFilter modifies HTTP response accordingly, for example a redirection to error page, or displaying a captcha along a login form can be triggered. 

It is worth noticing, that HTTP pipeline is a hot-path and therefore all time-consuming calculations should be performed asynchronously to the pipeline. The plugin offers such processing by design. 

### Rules
To obtain high-configurability and control, we decided to introduce concept of rules. We are not using any rule-based system with inference algorithms, the idea is not to have a huge rule system. For now it's a concept for structuring processing logic and control.

We use rules to determine what actions to perform based on the request and it's metadata. These rules can prioritize certain actions, combine them etc.

We also use rules to for defining protection mechanisms. Various rules can be created, for example: 
   *  If user failed to login for in last 5 tries, display a captcha
   *  If login success to fail ratio is <60%, display captcha for all IPs that match that criteria
   *  If user uses leaked password and his IP comes from suspicious provider, log an incident and redirect the request
   *  If user-agent string has a login success rate <40%, display captcha to all requests with that user-agent string

### Event store
We decided not to force developers to use any time-series database and we created a small cyclic buffer in-memory. Such in-memory store will provide enough state for algorithms to detect credential stuffing, but it will not provide persistence and scalability. For that, for now, developers would have to integrate their own solution, whether that's an SQL database or databases like influxdb, kafka or redis.   

### Pwned credentials
Attackers use leaked credentials from data breaches. It makes sense to check whether credentials from data breach are used for login attempt. These credentials can be scraped from the dark web, bought, or found on security websites. What action you perform on match, is defined by your rules, you can display captcha for that IP or you can warn a user about leaked credentials after you make sure this is a genuine user. 

We provide checking of hashes of credentials with a HTTP filter and bloom filter algorithm implementation. It offers great performance and can be used on hot-path, [read more here](https://github.com/xword/nixer-spring-plugin/blob/master/bloom-filter). We provide bloom file that contains credentials from [haveibeenpwned.com](https://haveibeenpwned.com/Passwords) but also we provide [tool for generating your own bloom filters](https://github.com/xword/nixer-spring-plugin/tree/master/bloom-tool) with credentials you found on your own.

### Suspicious IPs
Rating an IP can be a strong feature for attack detection algorithms. Or it can be used on its own to display captcha or monitor behavior. For country based services, maybe you expect traffic only from certain countries, or your application operates in an internal network and you expect traffic only from certain IPs. You can also obtain lists suspicious IPs from paid services same way crackers do etc. 

Some credential stuffing attacks use cloud providers to generate traffic, and it can create a visible pattern because rarely genuine users use cloud to services to access websites (it can happen though with virtual desktops). We provide an IP range filter, which checks whether IP of the user corresponds to configured lists. Most big cloud providers publish their IP addresses. We gathered these IPs in a file that IP range filter uses and also provide [simple python script](https://github.com/xword/nixer-spring-plugin/tree/master/scripts/ip_cloud_ranges) to extract current IP lists. You can find more information [here](https://github.com/xword/nixer-spring-plugin/tree/master/core).

### Stigma tokens
Credential stuffing attack usually involves proxies. From the application perspective, proxies look like new devices that suddenly show up. You can observe such scenario by introducing a token that is send back to the user's browser. It can be stored in a Cookie. This simple mechanism has powerful ability of detecting proxies and is an important feature for statistics and machine learning.

### Monitoring
By default, we publish events to micrometer. Also, we provide integration with Elastic stack. Because we use loosely coupled event-based architecture, you can extend logging and reporting mechanism to suit your needs. You can also configure rules to report, log, audit log any logging behavior you're interested in.

### Protection by Captcha
Google's Captcha V2 is a solid mechanism. We provide an example integration with Spring Security login form. You can configure the system to use other captcha providers. Sometime you may only wish to log suspicious behavior or maybe you want to redirect. 

## Licensing

Nixer credential stuffing plugin is an open source project licensed under MIT.



