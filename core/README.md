
# IP metadata provider
## Reasoning
 Credentials stuffing attack never come from single IP, since it would be too easy to mitigate such attack using rate limiting. 

 Instead attacker would use range of IP's to make detention harder. Such IP could be:
  - residential - ISP provided
  - proxies / VPN
  - cloud providers
  - data centers
  
  You could expect that Your users will use first two. 
  It may not make sense that your users are coming from IP that belong to cloud provider (eg. AWS). In cloud world attacker could spent little 
  money to provision many slave machines with unique IP addresses.  
  
  With this feature enabled it will lookup request IP in defined file and provide lookup result to rules which could execute arbitrary behavior
  eg. return captcha or error.   
    
  More sophisticated attacker could use eg. botnets to get access to residential ip addresses. In such case detection based on IP type would work. 
  
## Usage
### Flow
  Ip metadata provider is augmented filter which is added to nixer filter chain. IpMetadata object is set as attribute in servlet request under key 
  `nixer.ip.metadata`. Those attributes are referred to as facts. Further processing of rules determines behaviors that should be executed 
  based on gathered facts.
  
### Preparation
  To start with You need to prepare file with IP ranges. 
  We created script to automate process of extracting IP ranges for biggest cloud providers. It gathers IP ranges for both IPv4 and IPv6 
  that have been officially published by cloud providers. 
  File is just JSON so you could modify it at your will.
  
  You could either use already generated file by downloading it from [link](https://nixer.io/ip-ranges.json) **TODO put real location**
  or generate it yourself by running python script `cloud-ip-ranges.py` found in `config/ip_cloud_ranges`.
  
### Enable
   To enable IP metadata provider set below properties for your application.
     
   ```properties
   nixer.filter.ip.enabled=true
   nixer.filter.ip.ip-prefixes-path=classpath:ip-ranges.json
   ``` 
  
  Path to IP range file could be given for classpath resource or file. 
  
  We recommend using file external to application package to make modification to it possible without having to repackage entire application.  
  
### Behavior
  To control behavior for IP range match you need to set target behavior for (find better name) `ipBlacklisting` rule. You may also create custom rule 
  if you want to create more complex conditions.  

TODO
put example how to set behavior

TODO create section describing overall architecture and naming. eg. nixer filter, execution filter, facts etc.