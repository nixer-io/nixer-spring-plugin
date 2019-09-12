"""Cloud IP Ranges.

Usage:
  cloud_ip_ranges.py [--ipv4] [--ipv6][-o <file>] [--cloud <cloud>...] [-v]
  cloud_ip_ranges.py (-h | --help)
  cloud_ip_ranges.py --version

Options:
  -h --help         Show this screen.
  --version         Show version.
  -o --output FILE  Output file [default: ip-ranges.json].
  --ipv4            Fetch IPv4 addresses.
  --ipv6            Fetch IPv4 addresses.
  --cloud=<c>       Select cloud to fetch IP ranges from.
  -v --verbose      Verbose mode.
"""
import requests
from requests.exceptions import HTTPError
import functools
import operator
import subprocess
import json
from datetime import datetime
from docopt import docopt
from bs4 import BeautifulSoup
import re

def fetchPage(url):
    try:
        response = requests.get(url)
        response.raise_for_status()

        return response.content
    except HTTPError as http_err:
        print(f'HTTP error occurred: {http_err}')  # Python 3.6
    except Exception as err:
        print(f'Other error occurred: {err}')  # Python 3.6
    else:
        print('Success!')


def fetchJson(url):
    try:
        response = requests.get(url)
        response.raise_for_status()

        return response.json()
    except HTTPError as http_err:
        print(f'HTTP error occurred: {http_err}')  # Python 3.6
    except Exception as err:
        print(f'Other error occurred: {err}')  # Python 3.6
    else:
        print('Success!')


def fetchText(url):
    try:
        response = requests.get(url)
        response.raise_for_status()

        return response.text
    except HTTPError as http_err:
        print(f'HTTP error occurred: {http_err}')  # Python 3.6
    except Exception as err:
        print(f'Other error occurred: {err}')  # Python 3.6
    else:
        print('Success!')


def unique_sorted(elements):
    unique = set(elements)
    sorted = list(unique)
    sorted.sort()

    return sorted


# https://docs.aws.amazon.com/general/latest/gr/aws-ip-ranges.html
def fetchAwsIpRanges():
    url = 'https://ip-ranges.amazonaws.com/ip-ranges.json'
    print("Fetching IP ranges from AWS")
    ips = fetchJson(url)

    ipv4_ranges = [ prefix["ip_prefix"] for prefix in ips["prefixes"] ]
    ipv6_ranges = [ prefix["ipv6_prefix"] for prefix in ips["ipv6_prefixes"] ]

    print(f'Found prefixes IPv4: {len(ipv4_ranges)} IPv6: {len(ipv6_ranges)}')

    ipv4_ranges = unique_sorted(ipv4_ranges)
    ipv6_ranges = unique_sorted(ipv6_ranges)

    return {
        "name": "aws",
        "ipv4_prefixes": ipv4_ranges,
        "ipv6_prefixes": ipv6_ranges
    }


# {
#   "changeNumber": 83,
#   "cloud": "Public",
#   "values": [
#     {
#       "name": "AzureActiveDirectory",
#       "id": "AzureActiveDirectory",
#       "properties": {
#         "changeNumber": 4,
#         "region": "",
#         "platform": "Azure",
#         "systemService": "AzureAD",
#         "addressPrefixes": [
#           "13.64.151.161/32"
#         ]
#       }
#    }
#   ]
# }
# https://docs.microsoft.com/en-us/azure/virtual-network/public-ip-address-prefix
def fetchAzureIpRanges():
    url = 'https://download.microsoft.com/download/7/1/D/71D86715-5596-4529-9B13-DA13A5DE5B63/ServiceTags_Public_20190909.json'
    print("Fetching IP ranges from Azure")
    ips = fetchJson(url)

    ipv4_ranges = []
    for service in ips["values"]:
        prefixes = service["properties"]["addressPrefixes"]
        ipv4_ranges.extend(prefixes)

    ipv6_ranges = []

    print(f'Found prefixes IPv4: {len(ipv4_ranges)} IPv6: {len(ipv6_ranges)}')

    ipv4_ranges = unique_sorted(ipv4_ranges)
    ipv6_ranges = unique_sorted(ipv6_ranges)

    return {
        "name": "Azure",
        "ipv4_prefixes": ipv4_ranges,
        "ipv6_prefixes": ipv6_ranges
    }


# https://www.cloudflare.com/ips/
def fetchCloudflareIpRanges():
    ipv4_url = 'https://www.cloudflare.com/ips-v4'
    ipv6_url = 'https://www.cloudflare.com/ips-v6'

    print("Fetching Ip from Cloudflare")
    ipv4_text = fetchText(ipv4_url)
    ipv6_text = fetchText(ipv6_url)

    ipv4_ranges = [ line for line in ipv4_text.splitlines() ]
    ipv6_ranges = [ line for line in ipv6_text.splitlines() ]

    print(f'Found prefixes IPv4: {len(ipv4_ranges)} IPv6: {len(ipv6_ranges)}')

    ipv4_ranges = unique_sorted(ipv4_ranges)
    ipv6_ranges = unique_sorted(ipv6_ranges)

    return {
        "name": "cloudflare",
        "ipv4_prefixes": ipv4_ranges,
        "ipv6_prefixes": ipv6_ranges
    }


def run(commands):
    return subprocess.run(commands, capture_output=True)


# Google https://cloud.google.com/compute/docs/faq#find_ip_range
def fetchGceIpRanges():
    print("Fetching IP ranges from GCE")

    output = str(run(["dig", "@8.8.8.8", "-t", "TXT", "_cloud-netblocks.googleusercontent.com"]).stdout)

    answer = [line for line in output.splitlines() if "v=spf1" in line]
    servers = [ section.replace("include:", "") for section in answer[0].split(" ") if section.startswith("include:")]

    ipv4_ranges = [ ]
    ipv6_ranges = [ ]

    for server in servers:
        answer = str(run(["dig", "@8.8.8.8", "-t", "TXT", server]).stdout)
        tokens = answer.split(" ")

        ipv4_prefixes = [ token.replace("ip4:", "") for token in tokens if token.startswith("ip4:") ]
        ipv4_ranges.extend(ipv4_prefixes)

        ipv6_prefixes = [ token.replace("ip6:", "") for token in tokens if token.startswith("ip6:") ]
        ipv6_ranges.extend(ipv6_prefixes)


    print(f'Found prefixes IPv4: {len(ipv4_ranges)} IPv6: {len(ipv6_ranges)}')

    ipv4_ranges = unique_sorted(ipv4_ranges)
    ipv6_ranges = unique_sorted(ipv6_ranges)

    return {
        "name": "gce",
        "ipv4_prefixes": ipv4_ranges,
        "ipv6_prefixes": ipv6_ranges
    }


# IBM https://cloud.ibm.com/docs/infrastructure/virtual-router-appliance?topic=hardware-firewall-dedicated-ibm-cloud-ip-ranges
def fetchIBMIpRanges():
    print("Fetching IP ranges from IBM")

    url = "https://raw.githubusercontent.com/ibm-cloud-docs/hardware-firewall-dedicated/master/ips.md"
    oracle_docs = fetchText(url)
    
    ipv4_ranges = []
    for line in oracle_docs.splitlines():
        for prefix in re.finditer(r'(\d{1,3}(\.\d{1,3}){3}/\d{1,2})', line):
            ipv4_ranges.append(prefix.group(0))

    
    ipv6_ranges = []

    print(f'Found prefixes IPv4: {len(ipv4_ranges)} IPv6: {len(ipv6_ranges)}')

    ipv4_ranges = unique_sorted(ipv4_ranges)
    ipv6_ranges = unique_sorted(ipv6_ranges)

    return {
        "name": "ibm",
        "ipv4_prefixes": ipv4_ranges,
        "ipv6_prefixes": ipv6_ranges
    }


# Oracle https://docs.cloud.oracle.com/iaas/Content/General/Concepts/addressranges.htm
def fetchOracleCloudIpRanges():
    print("Fetching IP ranges from Oracle Cloud")

    url = "https://docs.cloud.oracle.com/iaas/Content/General/Concepts/addressranges.htm"
    oracle_page = fetchPage(url)
    soup = BeautifulSoup(oracle_page, 'html.parser')

    items = soup.find_all('div', class_='uk-accordion-content')

    prefix_li = [] 
    for item in items:
        prefix_li.extend(item.select("ul li"))

    prefixes = [li.get_text().split()[0] for li in prefix_li]

    ipv4_ranges = prefixes
    ipv6_ranges = [ ]

    print(f'Found prefixes IPv4: {len(ipv4_ranges)} IPv6: {len(ipv6_ranges)}')

    ipv4_ranges = unique_sorted(ipv4_ranges)
    ipv6_ranges = unique_sorted(ipv6_ranges)

    return {
        "name": "oracle",
        "ipv4_prefixes": ipv4_ranges,
        "ipv6_prefixes": ipv6_ranges
    }


CLOUDS = {
    "aws": fetchAwsIpRanges,
    "azure": fetchAzureIpRanges,
    "cloudflare": fetchCloudflareIpRanges,
    "gce": fetchGceIpRanges,
    "oracle": fetchOracleCloudIpRanges,
    "ibm": fetchIBMIpRanges
}


def cloud_fetchers(selected_clouds):
    selected_clouds = selected_clouds if selected_clouds else CLOUDS.keys()
    for cloud in selected_clouds:
        if cloud not in CLOUDS.keys():
            raise NameError(f'Unknown cloud {cloud}')

    return [CLOUDS[cloud] for cloud in selected_clouds]


def main(args):
    fetchers = cloud_fetchers(args['--cloud'])

    (ipv4, ipv6) = (args['--ipv4'], args['--ipv6']) if args['--ipv4'] or args['--ipv6'] else (True, True)

    sets = []
    for fetcher in fetchers:
        ip_ranges = fetcher()
        ip_ranges['timestamp'] = datetime.utcnow().strftime('%Y-%m-%dT%H:%M:%SZ')
        if not ipv4:
            ip_ranges['ipv4_prefixes'] = []
        if not ipv6:
            ip_ranges['ipv6_prefixes'] = []
        
        if args['--verbose']:
            print(ip_ranges)
        sets.append(ip_ranges)

    file_object = open(args['--output'], 'w')
    json.dump({ "ranges": sets}, file_object, indent=4)


if __name__ == '__main__':
    arguments = docopt(__doc__, version='Cloud IP Ranges 0.1')
    main(arguments)
