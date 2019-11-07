# Cloud Ip ranges

## Reason
 Purpose of this script is to gather ip ranges for cloud providers. 
 
 Supported cloud providers:
  - AWS
  - Azure
  - Google Cloud Engine
  - IBM cloud
  - Cloudflare
  - Oracle cloud
  
## Usage
1. Make sure you have `pipenv` installed [link](https://github.com/pypa/pipenv)
2. Setup python virtual env 
    ```
    pipenv install
    ```
3. Run script
    ```
    pipenv run python cloud-ip-ranges.py
    ```
4. See help for more options
    ```
    pipenv run python cloud-ip-ranges.py --help
    ```