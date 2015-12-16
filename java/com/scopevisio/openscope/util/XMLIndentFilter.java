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
package com.scopevisio.openscope.util;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;


public class XMLIndentFilter extends XMLFilterImpl {
    
    private static final String indentString = "  ";
    
    private int indent = 0;
    private String lastStart;
    private StringBuilder chars = new StringBuilder();
    
    public XMLIndentFilter() {
    }
    
    private void characters(String s) throws SAXException {
        super.characters(s.toCharArray(), 0, s.length());
    }
    
    @Override
    public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {
        lastStart = name;
        characters("\n");
        for (int i = 0; i < indent; ++i)
            characters(indentString);
        super.startElement(uri, localName, name, atts);
        ++indent;
    }
    
    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        --indent;
        boolean simpleElement = false;
        String text = chars.toString().trim();
        if (text.length() > 0) {
            characters(text);
            simpleElement = true;
        } else if (lastStart != null && lastStart.equals(name)) {
            simpleElement = true;
        }
        if (simpleElement) {
            super.endElement(uri, localName, name);
        } else {
            characters("\n");
            for (int i = 0; i < indent; ++i)
                characters(indentString);
            super.endElement(uri, localName, name);
        }
        chars = new StringBuilder();
    }
    
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        chars.append(ch, start, length);
    }
    
    
}
