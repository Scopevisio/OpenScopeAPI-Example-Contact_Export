# Copyright (c) 2015, Scopevisio AG
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without modification,
# are permitted provided that the following conditions are met:
#
# 1. Redistributions of source code must retain the above copyright notice, this
# list of conditions and the following disclaimer.
#
# 2. Redistributions in binary form must reproduce the above copyright notice, this
# list of conditions and the following disclaimer in the documentation and/or other
# materials provided with the distribution.
#
# 3. Neither the name of the copyright holder nor the names of its contributors may
# be used to endorse or promote products derived from this software without specific
# prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
# ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
# WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
# IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
# INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
# NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
# PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
# WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.
#!/bin/bash

BASE_URL="https://appload.scopevisio.com"

show_help () {
    # basic command line helper method 
    [ ! -z "$1" ] && echo "$1"
	echo -e "\nUsage: `basename $0` -c <customerid> -u <username>\n"
	echo -e "\t-c <customerid>\tPublic scopevisio customer ID, e.g., 2000000."
	echo -e "\t-u <username>\tLogin username, e.g., user@example.com."
	echo
	exit 1
}

encode_body_parts () {
    # encodes some characters that are not allowed in SOAP bodies 
    echo $( sed -e "s/&/&amp;/g" <<< "$1" )
}

# parse the command line 
while getopts "hc:u:" opt; do
    case "$opt" in
    h)
        show_help
        ;;
	c)  SVCID=$OPTARG
        ;;
	u)  SVUSER=$OPTARG
        ;;
	*)	show_help
		;;
	esac
done

# check if all is setup correctly 
[ -z $SVCID ] || [ -z $SVUSER ] && \
show_help "Customer ID and/or username not set."
[ -z $( command -v curl ) ] && show_help "You need curl for this script."

# ask for password 
read -s -p "Enter your scopevisio password: " SVPASS
SVPASS=$( encode_body_parts "$SVPASS" )
echo ""

# step 1: find an organization 
post_body="<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">
    <SOAP-ENV:Header/>
    <SOAP-ENV:Body>
        <ns1:req xmlns:ns1=\"http://www.scopevisio.com/\">
            <authn>
                <customer>${SVCID}</customer>
                <user>${SVUSER}</user>
                <pass>${SVPASS}</pass>
            </authn>
        </ns1:req>
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>"

response=$( curl -X POST -s \
"${BASE_URL}/api/soap/accounting/accounting.GetOrganisations" \
--data-raw "$post_body" )

# extract first element from response array 
response=$( sed -e "s/,.*//g" -e "s/^[^\"]\"//" -e "s/\"[^\"]$//"<<< $response ) 
echo "Found organization: '$response'" 

# step 2: get contacts created within the last 100 days 
since_timestamp="$( date  --date="100 days ago" "+%s" )000" # 100 days ago 
organisation=$( encode_body_parts "$response" )
post_body="<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">
    <SOAP-ENV:Header/>
    <SOAP-ENV:Body>
        <ns1:req xmlns:ns1=\"http://www.scopevisio.com/\">
            <authn>
                <customer>${SVCID}</customer>
                <user>${SVUSER}</user>
                <pass>${SVPASS}</pass>
                <language>de_DE</language>
                <organisation>${organisation}</organisation>
            </authn>
            <args>
                <createdSinceTimestamp>${since_timestamp}</createdSinceTimestamp>
            </args>
        </ns1:req>
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>"

# send request and format output to make it more readable
echo "Found contacts:"
curl -X POST -s "${BASE_URL}/api/soap/contact/Contact.exportExtendedCSV" \
--data-raw "$post_body" |\
sed -e "s/.*<data>//g" -e "s/<\/data>.*//g" 

echo "" # fin.
