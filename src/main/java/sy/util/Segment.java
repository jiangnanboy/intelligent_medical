package sy.util;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import utils.CollectionUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author sy
 * @date 2022/7/21 22:22
 */
public class Segment {

    /**
     * text segmentation
     * @param text
     * @param posi
     * @return
     */
    public static List<Entry> hanlpSegment(String text, boolean posi) {
        List<Entry> entryList;
        com.hankcs.hanlp.seg.Segment seg = HanLP.newSegment();
        seg.enableOffset(true);
        List<Term> termList = seg.seg(text);
        if(posi) {
            entryList = termList.stream().map(term -> new Entry(term.word, term.nature.toString(), term.offset)).collect(Collectors.toList());
        } else {
            entryList = termList.stream().map(term -> new Entry(term.word, term.nature.toString())).collect(Collectors.toList());
        }

        return entryList;
    }

    /**
     * text ner
     * @param text
     * @param posi
     * @return
     */
    public static List<Entry> hanlpNer(String text, boolean posi) {
        List<Entry> entryList;
        com.hankcs.hanlp.seg.Segment seg = HanLP.newSegment().enablePlaceRecognize(true).enableOrganizationRecognize(false).enableCustomDictionaryForcing(true);
        seg.enableOffset(true);
        List<Term> termList = seg.seg(text);
        if(posi) {
            entryList = termList.stream().map(term -> new Entry(term.word, term.nature.toString(), term.offset)).collect(Collectors.toList());
        } else {
            entryList = termList.stream().map(term -> new Entry(term.word, term.nature.toString())).collect(Collectors.toList());
        }

        return entryList;
    }

    /**
     * split sentence by punctuation
     * @param text
     * @return
     */
    public static List<String> splitSentence(String text){
        List<String> sentences = CollectionUtil.newArrayList();
        String regEx = "[。！？；：?!:;\n\r]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(text);
        String[] sent = p.split(text);
        int sentLen = sent.length;
        if(sentLen > 0){
            int count = 0;
            while(count < sentLen){
                if(m.find()){
                    sent[count] += m.group();
                }
                count ++;
            }
        }
        for(String sentence : sent){
            sentence = sentence.replaceAll("(&rdquo;|&ldquo;|&mdash;|&lsquo;|&rsquo;|&middot;|&quot;|&darr;|&bull;)", "");
            sentences.add(sentence.trim());
        }
        return sentences;
    }

}

