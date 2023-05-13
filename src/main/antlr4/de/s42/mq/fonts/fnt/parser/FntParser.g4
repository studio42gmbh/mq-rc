/*
 * The MIT License
 *
 * Copyright 2021 Studio 42 GmbH (https://www.s42m.de).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
grammar FntParser;

font : info common ( pageR )+ ( kernings )? NEWLINE* EOF ;

/*
 * See http://www.angelcode.com/products/bmfont/doc/file_format.html
 */

/* info face="Calibri Light" size=64 bold=0 italic=0 charset="" unicode=0 stretchH=100 smooth=1 aa=1 padding=7,7,7,7 spacing=-2,-2 */

info : 'info' 
	'face' EQUALS face = string
	'size' EQUALS size = integer
	'bold' EQUALS bold = bool
	'italic' EQUALS italic = bool
	'charset' EQUALS charset = string
	'unicode' EQUALS unicode = integer
	'stretchH' EQUALS stretchH = integer
	'smooth' EQUALS smooth = bool
	'aa' EQUALS aa = bool
	'padding' EQUALS paddingTop = integer COMMA paddingRight = integer COMMA paddingBottom = integer COMMA paddingLeft = integer
	'spacing' EQUALS spacingX = integer COMMA spacingY = integer
	NEWLINE+ ;


/* common lineHeight=91 base=48 scaleW=512 scaleH=512 pages=1 packed=0 */

common : 'common' 
	'lineHeight' EQUALS lineHeight = integer
	'base' EQUALS base = integer
	'scaleW' EQUALS scaleW = integer
	'scaleH' EQUALS scaleH = integer
	'pages' EQUALS pages = integer
	'packed' EQUALS packed = bool
	NEWLINE+ ;


/* page id=0 file="calibri-light.png" */

pageR : 'page'
	'id' EQUALS id = integer
	'file' EQUALS file = string
	NEWLINE chars ( charR )+ ;


/* chars count=110 */

chars : 'chars' 
	'count' EQUALS count = integer
	NEWLINE;


/* char id=0       x=0    y=0    width=0    height=0    xoffset=-7   yoffset=0    xadvance=12   page=0    chnl=0  */

charR : 'char'
	'id' EQUALS id = integer
	'x' EQUALS x = integer
	'y' EQUALS y = integer
	'width' EQUALS width = integer
	'height' EQUALS height = integer
	'xoffset' EQUALS xoffset = integer
	'yoffset' EQUALS yoffset = integer
	'xadvance' EQUALS xadvance = integer
	'page' EQUALS page = integer
	'chnl' EQUALS chnl = integer
	NEWLINE+ ;


/* kernings count=653 */

kernings : 'kernings' 
	'count' EQUALS count = integer
	NEWLINE+ ( kerning )* ;


/* kerning first=75 second=111 amount=-1 */

kerning : 'kerning'
	'first' EQUALS first = integer
	'second' EQUALS second = integer
	'amount' EQUALS amount = integer
	NEWLINE+ ;


/* data type rules */

string : STRING ;
integer : INTEGER ;
bool : INTEGER ;


/* literals */

EQUALS : '=' ;
COMMA : ',' ;
INTEGER : [-]? [0-9]+ ;
STRING : '"' ( ~('\n'|'\r') )*? '"' { setText(getText().substring(1, getText().length() - 1).replace("\\\"", "\"")); };
NEWLINE : [\n] ;
WS : [ \t\r]+ -> skip ;