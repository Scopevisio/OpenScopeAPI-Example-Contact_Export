/**
Copyright (c) 2015, Scopevisio AG
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or other
materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors may
be used to endorse or promote products derived from this software without specific
prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
 */
package com.scopevisio.openscope;

import java.lang.reflect.Array;

/**
 * 
 * @author mfg8876
 * @author BastiTee
 *
 */
public class Example {
    
    public static final String DEFAULT_URL = "https://appload.scopevisio.com";
    
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Die Argumentenliste muss von genau 3 Elementen bestehen.");
            System.err.println("Jedes Element, das Leerraum enthält, muss durch Anführungszeichen eingeschlossen sein.");
            System.err.println("Any element containing whitespace must be quoted");
            System.err.println("[Kunde] [Benutzer] [Kennwort]");
            System.err.println("Kunde entspricht der veröffentlichen Kundennummer bei Scopevisio");
            System.err.println("Beispiel zur Argumentenliste");
            System.err.println("2000000 user@example.com password");
            return;
        }
        String url = System.getProperty("com.scopevisio.openscope.webservice.url", DEFAULT_URL);
        try {
        	String organisation = GetOrganisations.getFirstOrganisation(GetOrganisations.getOrganisations(prepend(url, args)));
            String soapReply = ContactExportExtendedCsv.getContacts(prepend(url, prepend(organisation, args)));
            System.out.println("Contacts created within the last 100 days");
            System.out.println("=========================================");
            System.out.println(soapReply);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    @SuppressWarnings("unchecked")
    public static <E> E[] prepend(E value, E[] values) {
        E[] array = (E[]) Array.newInstance(value.getClass(), values.length + 1);
        array[0] = value;
        System.arraycopy(values, 0, array, 1, values.length);
        return array;
    }

}
