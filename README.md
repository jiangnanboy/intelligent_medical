# intelligent medical
尝试构建一个以疾病为中心的智慧医疗项目，整合搜索、推荐、图谱、问答以及语音等功能，形成一个较为完整的解决方案。

# 项目介绍

本项目中的医疗数据来自[QASystemOnMedicalKG](https://github.com/liuhuanyong/QASystemOnMedicalKG) 。  
数据中一共有7类总数为4.4万的医药实体，10类总数约27万实体关系的知识图谱，具体实体以及关系的数据结构介绍见以上或者[这里](https://github.com/jiangnanboy/intelligent_medical/medical_data.md) 。
本项目预计有以下几个核心功能：
1) 关于医疗搜索
2) 关于医疗图谱
3) 关于医疗问答
4) 关于医疗语音
5) 关于医疗查询自动补全
6) 关于医疗相关查询推荐
7) 关于医疗相似或相关推荐
8) 关于医疗实体抽取
9) 关于医疗词云
10) 关于医疗主题挖掘
11) ...

# 图谱的构建以及数据存储
这里分为两部分，一部分医疗数据以图谱的形式存入neo4j中；另一部分医疗数据以json格式存入elasticsearch中。

1、存入neo4j（micro_service/service/NeoUtilService.java）

将节点和关系批量导入neo4j。

如下截图：

![image](https://github.com/jiangnanboy/intelligent_medical/blob/master/images/neo4j.png)

2、存入elasticsearch（micro_service/service/EsUtilService.java）

将医疗数据以json格式存入elasticsearch中，这里包括自动构建mapping（mapping格式resources/medical_mapping.json）。

如下截图：

![image](https://github.com/jiangnanboy/intelligent_medical/blob/master/images/es.png)


# 项目运行方式
利用微服务(spark-core)方式启动：

1、配置要求：见requirement.txt。  
2、服务启动：micro_service/IntelligentServer.java。

# 结果展示
1、关于医疗搜索，这里通过elasticsearch搜索疾病名，并以分页形式返回结果：

![image](https://github.com/jiangnanboy/intelligent_medical/blob/master/images/search.png)

2、关于医疗图谱，在neo4j中返回与某疾病相关三元组信息：

![image](https://github.com/jiangnanboy/intelligent_medical/blob/master/images/kg_triples.png)

# Todo

- [ ] 关于医疗问答
- [ ] 关于医疗语音
- [ ] 关于医疗查询自动补全
- [ ] 关于医疗相关查询推荐
- [ ] 关于医疗相似或相关推荐
- [ ] 关于医疗实体抽取
- [ ] 关于医疗词云
- [ ] 关于医疗主题挖掘
- [ ] ...

# 总结
1、本项目尝试构建以疾病为中心的智慧医疗项目，并以微服务的形式提供接口服务。     
2、本项目会持续优化并加入新功能。

# contact

如有搜索、推荐、nlp以及大数据挖掘等问题或合作，可联系我：

1、我的github项目介绍：https://github.com/jiangnanboy

2、我的博客园技术博客：https://www.cnblogs.com/little-horse/

3、我的QQ号:2229029156

