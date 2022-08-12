# intelligent medical
尝试构建一个以疾病为中心的智慧医疗项目，整合搜索、推荐、图谱、问答以及语音等功能，形成一个较为完整的解决方案。

# 项目介绍

本项目中的医疗数据来自[QASystemOnMedicalKG](https://github.com/liuhuanyong/QASystemOnMedicalKG) 。  
数据中一共有7类总数为4.4万的医药实体，10类总数约27万实体关系的知识图谱，具体实体以及关系的数据结构介绍见以上或者[这里](https://github.com/jiangnanboy/intelligent_medical/blob/master/medical_data.md) 。
本项目预计有以下几个核心功能：
1) 搜索
2) 图谱
3) 问答
4) 语音
5) 查询自动补全
6) 相关查询推荐
7) 相关推荐
8) 实体抽取
9) 词云
10) 主题挖掘
11) ...

# 图谱的构建以及数据存储
这里分为两部分，一部分医疗数据以图谱的形式存入neo4j中；另一部分医疗数据以json格式存入elasticsearch中。

1、数据处理见（sy/process）,存入neo4j中的数据被处理成节点和关系的文本文件（resources/kg_input_data/），便于快速批量导入。

2、存入neo4j（micro_service/service/NeoUtilService.java）

将节点和关系批量导入neo4j。

如下截图：

![image](https://github.com/jiangnanboy/intelligent_medical/blob/master/images/schema.png)

3、存入elasticsearch（micro_service/service/EsUtilService.java）

将医疗数据以json格式存入elasticsearch中，这里包括自动构建mapping（mapping格式resources/medical_mapping.json）。

如下截图：

![image](https://github.com/jiangnanboy/intelligent_medical/blob/master/images/es.png)

# 项目运行方式
利用微服务(sparkjava)方式启动：

1、配置要求：见requirement.txt。  
2、服务启动：micro_service/IntelligentServer.java。

# 结果展示
1、关于医疗搜索，这里通过elasticsearch搜索疾病名称，并以分页形式返回相关结果

   访问：http://localhost:4567/engine/search?query=鼻炎&currentPage=1&size=10
```
[
  {
    "name": "鼻炎",
    "id": "5bb57901831b973a137e6124"
  },
  {
    "name": "急性鼻炎",
    "id": "5bb578c9831b973a137e469f"
  },
  {
    "name": "小儿鼻炎",
    "id": "5bb578d8831b973a137e4d6d"
  },
  {
    "name": "慢性鼻炎",
    "id": "5bb578dd831b973a137e4fab"
  },
  {
    "name": "常年性鼻炎",
    "id": "5bb578c1831b973a137e42b3"
  },
  ...
]
```

2、关于医疗图谱，在neo4j中返回与某疾病相关的三元组信息，包括推荐药品、推荐食谱以及伴随症状等

   访问：http://localhost:4567/engine/kg?id=5bb57901831b973a137e6124
```
[{"left":{"id":"5bb57901831b973a137e6124","name":"鼻炎","label":"Disease"},"middle":"RECOMMAND_EAT","right":{"name":"黄花鱼粥","label":"Food"}},
{"left":{"id":"5bb57901831b973a137e6124","name":"鼻炎","label":"Disease"},"middle":"RECOMMAND_DRUG","right":{"name":"匹多莫德片","label":"Drug"}},
{"left":{"id":"5bb57901831b973a137e6124","name":"鼻炎","label":"Disease"},"middle":"RECOMMAND_EAT","right":{"name":"独脚金煲猪瘦肉","label":"Food"}},
{"left":{"id":"5bb57901831b973a137e6124","name":"鼻炎","label":"Disease"},"middle":"NEED_CHECK","right":{"name":"后鼻镜检查","label":"Check"}},
{"left":{"id":"5bb57901831b973a137e6124","name":"鼻炎","label":"Disease"},"middle":"NO_EAT","right":{"name":"沙丁鱼","label":"Food"}},...]
```

3、关于医疗问答，通过医疗疾病问答的形式，返回答案

访问：http://localhost:4567/engine/qa?question=全血细胞计数能查出啥来
    
```
问：全血细胞计数能查出啥来？
答：通常可以通过全血细胞计数检查出来的疾病有:眼球内炎;痴呆综合征;原发性免疫缺陷病;老年痴呆;老年收缩期高血压;交感性眼炎;小儿肝硬化;睾丸淋巴瘤;急性淋巴管炎;视网膜血管炎;宫颈妊娠;叶酸缺乏症;不稳定血红蛋白病;阿尔采末病;异常血红蛋白病;老年期抑郁症;成人类风湿性关节炎性巩膜炎;前房积血;高血压病伴发的精神障碍;类癌综合征;先天性心脏病;甲亢合并妊娠;低增生性急性白血病;蚕食性角膜溃疡;外阴-阴道-牙龈综合征;电击伤;叶酸缺乏所致贫血

问：什么人容易得高血压？？
答：高血压的易感人群包括：有高血压家族史，不良的生活习惯，缺乏运动的人群

问：肝病要吃啥药？
答：肝病宜食的食物包括有：鹅肉;鸡肉;鸡腿;鸡肝
   推荐食谱包括有:芝麻小米粥;黄豆小米粥;小米粉粥;小米蛋奶粥;人参小米粥;扁豆小米粥;鲜菇小米粥;小米红糖粥
   肝病通常的使用的药品包括：恩替卡韦分散片;维生素C片;二十五味松石丸;阿德福韦酯片;拉米夫定胶囊
...
```
# Todo

- [x] 问答
- [ ] 语音
- [ ] 查询自动补全
- [ ] 相关查询推荐
- [ ] 相关推荐
- [ ] 实体抽取
- [ ] 词云
- [ ] 主题挖掘
- [ ] ...

# 总结
1、本项目利用java尝试构建以疾病为中心的智慧医疗项目，并以微服务的形式提供接口服务。   

2、疾病相关的属性如：疾病简介、病因、预防以及治疗周期等长文本存储在elasticsearch中；其它以节点关系类型存储于neo4j中。

3、本项目会持续优化并加入新功能。

# contact

如有搜索、推荐、nlp以及大数据挖掘等问题或合作，可联系我：

1、我的github项目介绍：https://github.com/jiangnanboy

2、我的博客园技术博客：https://www.cnblogs.com/little-horse/

3、我的QQ号:2229029156

