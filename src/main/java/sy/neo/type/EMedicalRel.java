package sy.neo.type;

/**
 * @author sy
 * @date 2022/7/21 22:22
 */
public enum EMedicalRel {
    RECOMMAND_EAT, //疾病推荐食谱
    NO_EAT, //疾病忌吃食物
    DO_EAT, //疾病宜吃食物
    BELONGS_TO, //属于
    COMMON_DRUG, //疾病常用药品
    DRUGS_OF, //药品在售药品
    RECOMMAND_DRUG, //疾病推荐药品
    NEED_CHECK, //疾病所需检查
    HAS_SYMPTOM, //疾病症状
    ACOMPANY_WITH //疾病并发疾病
}
