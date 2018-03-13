import re
import urllib.request

html = urllib.request.urlopen('http://quote.eastmoney.com/stocklist.html').read().decode('gbk')
p = re.compile('<li><a target="_blank" href="http://quote.eastmoney.com/([a-z|0-9]{8}).html">([^<]*)</a></li>')
l = p.findall(html)

for item in l:
  print(item[0][2:], item[1][:-8])
