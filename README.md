# accountmanager

### 一、apk功能简介
使用本地存储，根据对称加密算法对密码进行加密，本地存储只保存密文，用户每次需要输入密钥进行解密。由于app本地不存储密码明文或者密钥，因此您输入的任何密钥都可以解密出密码，但只有输入的密钥与保存时输入的密钥相同时，才可以获得正确的密码

### 二、为啥应用名与用户名不做加密
常见的应用名当前是可以穷尽的，攻击者可以通过穷尽应用名，猜测出对应的密钥；用户名一般为手机号，当出现互联网app账号泄露时，手机号容易被攻击者获取，从而反推测出对应密钥

### 三、安全性
apk仅将账号信息保存本地，未申请网络权限，不上传任何信息;当需要保存本地文件时，将申请本地读写权限

### 四、当前功能及后续版本规划
当前1.0功能：
+ 增查 改(通过增间接实现)
+ 保存本地文件(保存路径在Download/passFile/下)
+ 从本地文件导入(目前仅在小米5s plus上成功，通过路径进行拼接，后续直接优化通过api获取)
+ 屏幕最下方打印相关信息  

版本演进计划：
+ 版本1.1 增加删除功能，优化导入功能， 完善屏幕信息打印内容，解决相关bug
+ 版本2.0 在更多手机上进行适配，对手机屏幕进行适配
+ 版本3.0 暂无计划

#### 注意事项：
+ 账号信息保存本地文件，是为了方便换手机时进行转移，不建议将账号信息上传至网盘或云，防止网盘或云信息泄露，增加风险
+ 密钥建议6位以上，密钥的复杂度决定被破解的难度，6位的复杂度为``` 90^6 < 100^6 ```=1万亿
+ 密码算法：位对称加密，如：
```
1.密码12345，密钥12，加密结果：1214141616，奇数位表示位算法，1为加，偶数位表示加的结果，密文计算过程：
密码位  | 密钥位  |  加密结果
1 | 1 | 12
2 | 2 | 14
3 | 1 | 14
4 | 2 | 16
5 | 1 | 16
```
是否需要开源协议，如果需要，遵循[996.icu](https://github.com/996icu/996.ICU/blob/master/LICENSE),与[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)开源协议

#### 求大佬指点
小哥初尝安卓开发，基础api也是现学现卖，望大佬多多提携，指点不足之处


#### 求打赏1元
小哥近半年来，经历了多件人生大事，感叹人生苦短，理应及时行乐，奈何苦无粮草之资，走过路过，求打赏1元，方可行走江湖  
支付宝扫码领红包                                 大佬打赏专用  
![红包](https://github.com/xiaoming11123/accountmanager/blob/master/pic/6.png) 
![红包](https://github.com/xiaoming11123/accountmanager/blob/master/pic/7.png)

#### 使用指南及对应截图
[跳转链接](https://github.com/xiaoming11123/accountmanager/blob/master/direct.md)
