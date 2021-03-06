#!/usr/bin/python    
from struct import unpack

def exactStock(fileName, code):  
    ofile = open(fileName,'rb')  
    buf=ofile.read()  
    ofile.close()  
    num=len(buf)  
    no=num/32  
    b=0  
    e=32  
    items = list()   
    ratio = 0
    preClose = 0
    for i in range(int(no)):  
        a=unpack('IIIIIfII',buf[b:e])  
        print(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7])
        year = int(a[0]/10000);  
        m = int((a[0]%10000)/100);  
        month = str(m);  
        if m <10 :  
            month = "0" + month;  
        d = (a[0]%10000)%100;  
        day=str(d);  
        if d< 10 :  
            day = "0" + str(d);  
        dd = str(year)+"-"+month+"-"+day  
        openPrice = a[1]/100.0  
        high = a[2]/100.0  
        low =  a[3]/100.0  
        close = a[4]/100.0  
        amount = a[5]/10.0  
        vol = a[6]  
        unused = a[7]  

#        if i == 0 :  
#            preClose = close  
#        ratio = round((close - preClose)/preClose*100, 2)  
#        preClose = close

        item=[code, dd, str(openPrice), str(high), str(low), str(close), str(ratio), str(amount), str(vol)]  
        items.append(item)  
        b=b+32  
        e=e+32  
          
    return items  
  
import sys
code = sys.argv[1]
print(exactStock('/Users/ylu/Downloads/vipdoc/sz/lday/sz%s.day' % code, code)[0])
