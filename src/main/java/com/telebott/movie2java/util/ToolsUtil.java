package com.telebott.movie2java.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.dao.FilterWordsDao;
import com.telebott.movie2java.entity.FilterWords;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Component
@Slf4j
public class ToolsUtil {
    private static ToolsUtil self;
    @Autowired
    private FilterWordsDao filterWordsDao;

    private static List<FilterWords> filterWords;
    public static final int TIME_OUT = 30;
    public static final int MAX_Black = 3;
    /**
     * 对象转Byte数组
     * @param obj
     * @return
     */
    public static byte[] objectToByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();
            bytes = byteArrayOutputStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return bytes;
    }

    /**
     * Byte数组转对象
     * @param bytes
     * @return
     */
    public static <T> T byteArrayToObject(byte[] bytes) {
        Object obj = null;
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            obj = objectInputStream.readObject();
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (byteArrayInputStream != null) {
                try {
                    byteArrayInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return (T) obj;
    }
    public static String getCamelPinYin(String hz) {
        return getCamelPinYin(hz, false);
    }
    public static String getCamelPinYin(String hz, boolean type) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        String m;
        StringBuilder r = new StringBuilder();
        try {
            for (char value : hz.toCharArray()) {
                // 判断是否为汉字字符
                if (Character.toString(value).matches("[\\u4E00-\\u9FA5]+")) {
                    // 取出该汉字全拼的第一种读音并连接到字符串m后
                    m = PinyinHelper.toHanyuPinyinStringArray(value, format)[0];
                } else {
                    // 如果不是汉字字符，直接取出字符并连接到字符串m后
                    m = Character.toString(value);
                }

                if (type) {
                    r.append(m.substring(0, 1).toUpperCase()).append(m.substring(1));
                } else {
                    r.append(m);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            log.error(e.getMessage(), e);
        }

        return r.toString();
    }

    /**
     * 简要说明：获取汉字首字母
     *
     * @param hz 	[需要转换的汉字]
     * @param type 	[是否大写]
     * @return java.lang.String
     */
    public static String getPinYinHeadChar(String hz, boolean type) {
        StringBuilder r = new StringBuilder();
        String s = "";
        for (char v : hz.toCharArray()) {
            // 判断是否为汉字字符
            if (Character.toString(v).matches("[\\u4E00-\\u9FA5]+")) {
                s = PinyinHelper.toHanyuPinyinStringArray(v)[0];
            } else {
                // 如果不是汉字字符，直接取出字符并连接到字符串m后
                s = Character.toString(v);
            }

            r.append(s != null ? s.substring(0, 1) : null);
        }

        return type ? r.toString().toUpperCase() : r.toString();
    }
    /**
     * 获取汉字串拼音首字母，英文字符不变
     * @param chinese 汉字串
     * @return 汉语拼音首字母
     */
    public static String getFirstSpell(String chinese) {
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        if(arr.length>0){
            if (arr[0] > 128) {
                try {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[0], defaultFormat);
                    if (temp != null) {
                        pybf.append(temp[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pybf.append(arr[0]);
            }
        }
        return pybf.toString().replaceAll("\\W", "").trim().toUpperCase();
    }
    public static ModelAndView errorHtml(String msg){
        ModelAndView error = new ModelAndView("payHtml/error");
        error.addObject("msg",msg);
        return error;
    }
    public static ModelAndView postHtml(String url, JSONObject params){
        ModelAndView post = new ModelAndView("payHtml/post");
        post.addObject("url",url);
        post.addObject("params",params);
        return post;
    }
    public static ModelAndView getHtml(String url){
        ModelAndView post = new ModelAndView("payHtml/get");
        post.addObject("url",url);
        return post;
    }
    public static ModelAndView waitHtml(){
        ModelAndView post = new ModelAndView("payHtml/wait");
//        post.addObject("url",url);
        return post;
    }
    @PostConstruct
    public void init(){
        self = this;
        rest();
    }
    public static void rest(){
        filterWords = self.filterWordsDao.findAll();
    }

    public static boolean checkChinese(String countname)
    {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(countname);
        if (m.find()) {
            return true;
        }
        return false;
    }
    public static String getRandomJianHan(int len) {
        String randomName = "";
        for (int i = 0; i < len; i++) {
            String str = null;
            int hightPos, lowPos; // 定义高低位
            Random random = new Random();
            hightPos = (176 + Math.abs(random.nextInt(39))); // 获取高位值
            lowPos = (161 + Math.abs(random.nextInt(93))); // 获取低位值
            byte[] b = new byte[2];
            b[0] = (new Integer(hightPos).byteValue());
            b[1] = (new Integer(lowPos).byteValue());
            try {
                str = new String(b, "GBK"); // 转成中文
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
            randomName += str;
        }
        return randomName;
    }
    /**方法2*/
    public static String randomName(boolean simple, int len) {
        String surName[] = {
                "赵","钱","孙","李","周","吴","郑","王","冯","陈","楮","卫","蒋","沈","韩","杨",
                "朱","秦","尤","许","何","吕","施","张","孔","曹","严","华","金","魏","陶","姜",
                "戚","谢","邹","喻","柏","水","窦","章","云","苏","潘","葛","奚","范","彭","郎",
                "鲁","韦","昌","马","苗","凤","花","方","俞","任","袁","柳","酆","鲍","史","唐",
                "费","廉","岑","薛","雷","贺","倪","汤","滕","殷","罗","毕","郝","邬","安","常",
                "乐","于","时","傅","皮","卞","齐","康","伍","余","元","卜","顾","孟","平","黄",
                "和","穆","萧","尹","姚","邵","湛","汪","祁","毛","禹","狄","米","贝","明","臧",
                "计","伏","成","戴","谈","宋","茅","庞","熊","纪","舒","屈","项","祝","董","梁",
                "杜","阮","蓝","闽","席","季","麻","强","贾","路","娄","危","江","童","颜","郭",
                "梅","盛","林","刁","锺","徐","丘","骆","高","夏","蔡","田","樊","胡","凌","霍",
                "虞","万","支","柯","昝","管","卢","莫","经","房","裘","缪","干","解","应","宗",
                "丁","宣","贲","邓","郁","单","杭","洪","包","诸","左","石","崔","吉","钮","龚",
                "程","嵇","邢","滑","裴","陆","荣","翁","荀","羊","於","惠","甄","麹","家","封",
                "芮","羿","储","靳","汲","邴","糜","松","井","段","富","巫","乌","焦","巴","弓",
                "牧","隗","山","谷","车","侯","宓","蓬","全","郗","班","仰","秋","仲","伊","宫",
                "宁","仇","栾","暴","甘","斜","厉","戎","祖","武","符","刘","景","詹","束","龙",
                "叶","幸","司","韶","郜","黎","蓟","薄","印","宿","白","怀","蒲","邰","从","鄂",
                "索","咸","籍","赖","卓","蔺","屠","蒙","池","乔","阴","郁","胥","能","苍","双",
                "闻","莘","党","翟","谭","贡","劳","逄","姬","申","扶","堵","冉","宰","郦","雍",
                "郤","璩","桑","桂","濮","牛","寿","通","边","扈","燕","冀","郏","浦","尚","农",
                "温","别","庄","晏","柴","瞿","阎","充","慕","连","茹","习","宦","艾","鱼","容",
                "向","古","易","慎","戈","廖","庾","终","暨","居","衡","步","都","耿","满","弘",
                "匡","国","文","寇","广","禄","阙","东","欧","殳","沃","利","蔚","越","夔","隆",
                "师","巩","厍","聂","晁","勾","敖","融","冷","訾","辛","阚","那","简","饶","空",
                "曾","毋","沙","乜","养","鞠","须","丰","巢","关","蒯","相","查","后","荆","红",
                "游","竺","权","逑","盖","益","桓","公","晋","楚","阎","法","汝","鄢","涂","钦",
                "岳","帅","缑","亢","况","后","有","琴","商","牟","佘","佴","伯","赏","墨","哈",
                "谯","笪","年","爱","阳","佟"};

        String doubleSurName[] = {"万俟","司马","上官","欧阳","夏侯","诸葛","闻人","东方",
                "赫连","皇甫","尉迟","公羊","澹台","公冶","宗政","濮阳","淳于","单于","太叔","申屠",
                "公孙","仲孙","轩辕","令狐","锺离","宇文","长孙","慕容","鲜于","闾丘","司徒","司空",
                "丌官","司寇","仉","督","子车","颛孙","端木","巫马","公西","漆雕","乐正","壤驷","公良",
                "拓拔","夹谷","宰父","谷梁","段干","百里","东郭","南门","呼延","归","海","羊舌","微生",
                "梁丘","左丘","东门","西门","南宫"};

        String[] word = {"一","乙","二","十","丁","厂","七","卜","人","入","八","九","几","儿","了","力","乃","刀","又",
                "三","于","干","亏","士","工","土","才","寸","下","大","丈","与","万","上","小","口","巾","山",
                "千","乞","川","亿","个","勺","久","凡","及","夕","丸","么","广","亡","门","义","之","尸","弓",
                "己","已","子","卫","也","女","飞","刃","习","叉","马","乡","丰","王","井","开","夫","天","无",
                "元","专","云","扎","艺","木","五","支","厅","不","太","犬","区","历","尤","友","匹","车","巨",
                "牙","屯","比","互","切","瓦","止","少","日","中","冈","贝","内","水","见","午","牛","手","毛",
                "气","升","长","仁","什","片","仆","化","仇","币","仍","仅","斤","爪","反","介","父","从","今",
                "凶","分","乏","公","仓","月","氏","勿","欠","风","丹","匀","乌","凤","勾","文","六","方","火",
                "为","斗","忆","订","计","户","认","心","尺","引","丑","巴","孔","队","办","以","允","予","劝",
                "双","书","幻","玉","刊","示","末","未","击","打","巧","正","扑","扒","功","扔","去","甘","世",
                "古","节","本","术","可","丙","左","厉","右","石","布","龙","平","灭","轧","东","卡","北","占",
                "业","旧","帅","归","且","旦","目","叶","甲","申","叮","电","号","田","由","史","只","央","兄",
                "叼","叫","另","叨","叹","四","生","失","禾","丘","付","仗","代","仙","们","仪","白","仔","他",
                "斥","瓜","乎","丛","令","用","甩","印","乐","句","匆","册","犯","外","处","冬","鸟","务","包",
                "饥","主","市","立","闪","兰","半","汁","汇","头","汉","宁","穴","它","讨","写","让","礼","训",
                "必","议","讯","记","永","司","尼","民","出","辽","奶","奴","加","召","皮","边","发","孕","圣",
                "对","台","矛","纠","母","幼","丝","式","刑","动","扛","寺","吉","扣","考","托","老","执","巩",
                "圾","扩","扫","地","扬","场","耳","共","芒","亚","芝","朽","朴","机","权","过","臣","再","协",
                "西","压","厌","在","有","百","存","而","页","匠","夸","夺","灰","达","列","死","成","夹","轨",
                "邪","划","迈","毕","至","此","贞","师","尘","尖","劣","光","当","早","吐","吓","虫","曲","团",
                "同","吊","吃","因","吸","吗","屿","帆","岁","回","岂","刚","则","肉","网","年","朱","先","丢",
                "舌","竹","迁","乔","伟","传","乒","乓","休","伍","伏","优","伐","延","件","任","伤","价","份",
                "华","仰","仿","伙","伪","自","血","向","似","后","行","舟","全","会","杀","合","兆","企","众",
                "爷","伞","创","肌","朵","杂","危","旬","旨","负","各","名","多","争","色","壮","冲","冰","庄",
                "庆","亦","刘","齐","交","次","衣","产","决","充","妄","闭","问","闯","羊","并","关","米","灯",
                "州","汗","污","江","池","汤","忙","兴","宇","守","宅","字","安","讲","军","许","论","农","讽",
                "设","访","寻","那","迅","尽","导","异","孙","阵","阳","收","阶","阴","防","奸","如","妇","好",
                "她","妈","戏","羽","观","欢","买","红","纤","级","约","纪","驰","巡","寿","弄","麦","形","进",
                "戒","吞","远","违","运","扶","抚","坛","技","坏","扰","拒","找","批","扯","址","走","抄","坝",
                "贡","攻","赤","折","抓","扮","抢","孝","均","抛","投","坟","抗","坑","坊","抖","护","壳","志",
                "扭","块","声","把","报","却","劫","芽","花","芹","芬","苍","芳","严","芦","劳","克","苏","杆",
                "杠","杜","材","村","杏","极","李","杨","求","更","束","豆","两","丽","医","辰","励","否","还",
                "歼","来","连","步","坚","旱","盯","呈","时","吴","助","县","里","呆","园","旷","围","呀","吨",
                "足","邮","男","困","吵","串","员","听","吩","吹","呜","吧","吼","别","岗","帐","财","针","钉",
                "告","我","乱","利","秃","秀","私","每","兵","估","体","何","但","伸","作","伯","伶","佣","低",
                "你","住","位","伴","身","皂","佛","近","彻","役","返","余","希","坐","谷","妥","含","邻","岔",
                "肝","肚","肠","龟","免","狂","犹","角","删","条","卵","岛","迎","饭","饮","系","言","冻","状",
                "亩","况","床","库","疗","应","冷","这","序","辛","弃","冶","忘","闲","间","闷","判","灶","灿",
                "弟","汪","沙","汽","沃","泛","沟","没","沈","沉","怀","忧","快","完","宋","宏","牢","究","穷",
                "灾","良","证","启","评","补","初","社","识","诉","诊","词","译","君","灵","即","层","尿","尾",
                "迟","局","改","张","忌","际","陆","阿","陈","阻","附","妙","妖","妨","努","忍","劲","鸡","驱",
                "纯","纱","纳","纲","驳","纵","纷","纸","纹","纺","驴","纽","奉","玩","环","武","青","责","现",
                "表","规","抹","拢","拔","拣","担","坦","押","抽","拐","拖","拍","者","顶","拆","拥","抵","拘",
                "势","抱","垃","拉","拦","拌","幸","招","坡","披","拨","择","抬","其","取","苦","若","茂","苹",
                "苗","英","范","直","茄","茎","茅","林","枝","杯","柜","析","板","松","枪","构","杰","述","枕",
                "丧","或","画","卧","事","刺","枣","雨","卖","矿","码","厕","奔","奇","奋","态","欧","垄","妻",
                "轰","顷","转","斩","轮","软","到","非","叔","肯","齿","些","虎","虏","肾","贤","尚","旺","具",
                "果","味","昆","国","昌","畅","明","易","昂","典","固","忠","咐","呼","鸣","咏","呢","岸","岩",
                "帖","罗","帜","岭","凯","败","贩","购","图","钓","制","知","垂","牧","物","乖","刮","秆","和",
                "季","委","佳","侍","供","使","例","版","侄","侦","侧","凭","侨","佩","货","依","的","迫","质",
                "欣","征","往","爬","彼","径","所","舍","金","命","斧","爸","采","受","乳","贪","念","贫","肤",
                "肺","肢","肿","胀","朋","股","肥","服","胁","周","昏","鱼","兔","狐","忽","狗","备","饰","饱",
                "饲","变","京","享","店","夜","庙","府","底","剂","郊","废","净","盲","放","刻","育","闸","闹",
                "郑","券","卷","单","炒","炊","炕","炎","炉","沫","浅","法","泄","河","沾","泪","油","泊","沿",
                "泡","注","泻","泳","泥","沸","波","泼","泽","治","怖","性","怕","怜","怪","学","宝","宗","定",
                "宜","审","宙","官","空","帘","实","试","郎","诗","肩","房","诚","衬","衫","视","话","诞","询",
                "该","详","建","肃","录","隶","居","届","刷","屈","弦","承","孟","孤","陕","降","限","妹","姑",
                "姐","姓","始","驾","参","艰","线","练","组","细","驶","织","终","驻","驼","绍","经","贯","奏",
                "春","帮","珍","玻","毒","型","挂","封","持","项","垮","挎","城","挠","政","赴","赵","挡","挺",
                "括","拴","拾","挑","指","垫","挣","挤","拼","挖","按","挥","挪","某","甚","革","荐","巷","带",
                "草","茧","茶","荒","茫","荡","荣","故","胡","南","药","标","枯","柄","栋","相","查","柏","柳",
                "柱","柿","栏","树","要","咸","威","歪","研","砖","厘","厚","砌","砍","面","耐","耍","牵","残",
                "殃","轻","鸦","皆","背","战","点","临","览","竖","省","削","尝","是","盼","眨","哄","显","哑",
                "冒","映","星","昨","畏","趴","胃","贵","界","虹","虾","蚁","思","蚂","虽","品","咽","骂","哗",
                "咱","响","哈","咬","咳","哪","炭","峡","罚","贱","贴","骨","钞","钟","钢","钥","钩","卸","缸",
                "拜","看","矩","怎","牲","选","适","秒","香","种","秋","科","重","复","竿","段","便","俩","贷",
                "顺","修","保","促","侮","俭","俗","俘","信","皇","泉","鬼","侵","追","俊","盾","待","律","很",
                "须","叙","剑","逃","食","盆","胆","胜","胞","胖","脉","勉","狭","狮","独","狡","狱","狠","贸",
                "怨","急","饶","蚀","饺","饼","弯","将","奖","哀","亭","亮","度","迹","庭","疮","疯","疫","疤",
                "姿","亲","音","帝","施","闻","阀","阁","差","养","美","姜","叛","送","类","迷","前","首","逆",
                "总","炼","炸","炮","烂","剃","洁","洪","洒","浇","浊","洞","测","洗","活","派","洽","染","济",
                "洋","洲","浑","浓","津","恒","恢","恰","恼","恨","举","觉","宣","室","宫","宪","突","穿","窃",
                "客","冠","语","扁","袄","祖","神","祝","误","诱","说","诵","垦","退","既","屋","昼","费","陡",
                "眉","孩","除","险","院","娃","姥","姨","姻","娇","怒","架","贺","盈","勇","怠","柔","垒","绑",
                "绒","结","绕","骄","绘","给","络","骆","绝","绞","统","耕","耗","艳","泰","珠","班","素","蚕",
                "顽","盏","匪","捞","栽","捕","振","载","赶","起","盐","捎","捏","埋","捉","捆","捐","损","都",
                "哲","逝","捡","换","挽","热","恐","壶","挨","耻","耽","恭","莲","莫","荷","获","晋","恶","真",
                "框","桂","档","桐","株","桥","桃","格","校","核","样","根","索","哥","速","逗","栗","配","翅",
                "辱","唇","夏","础","破","原","套","逐","烈","殊","顾","轿","较","顿","毙","致","柴","桌","虑",
                "监","紧","党","晒","眠","晓","鸭","晃","晌","晕","蚊","哨","哭","恩","唤","啊","唉","罢","峰",
                "圆","贼","贿","钱","钳","钻","铁","铃","铅","缺","氧","特","牺","造","乘","敌","秤","租","积",
                "秧","秩","称","秘","透","笔","笑","笋","债","借","值","倚","倾","倒","倘","俱","倡","候","俯",
                "倍","倦","健","臭","射","躬","息","徒","徐","舰","舱","般","航","途","拿","爹","爱","颂","翁",
                "脆","脂","胸","胳","脏","胶","脑","狸","狼","逢","留","皱","饿","恋","桨","浆","衰","高","席",
                "准","座","脊","症","病","疾","疼","疲","效","离","唐","资","凉","站","剖","竞","部","旁","旅",
                "畜","阅","羞","瓶","拳","粉","料","益","兼","烤","烘","烦","烧","烛","烟","递","涛","浙","涝",
                "酒","涉","消","浩","海","涂","浴","浮","流","润","浪","浸","涨","烫","涌","悟","悄","悔","悦",
                "害","宽","家","宵","宴","宾","窄","容","宰","案","请","朗","诸","读","扇","袜","袖","袍","被",
                "祥","课","谁","调","冤","谅","谈","谊","剥","恳","展","剧","屑","弱","陵","陶","陷","陪","娱",
                "娘","通","能","难","预","桑","绢","绣","验","继","球","理","捧","堵","描","域","掩","捷","排",
                "掉","堆","推","掀","授","教","掏","掠","培","接","控","探","据","掘","职","基","著","勒","黄",
                "萌","萝","菌","菜","萄","菊","萍","菠","营","械","梦","梢","梅","检","梳","梯","桶","救","副",
                "票","戚","爽","聋","袭","盛","雪","辅","辆","虚","雀","堂","常","匙","晨","睁","眯","眼","悬",
                "野","啦","晚","啄","距","跃","略","蛇","累","唱","患","唯","崖","崭","崇","圈","铜","铲","银",
                "甜","梨","犁","移","笨","笼","笛","符","第","敏","做","袋","悠","偿","偶","偷","您","售","停",
                "偏","假","得","衔","盘","船","斜","盒","鸽","悉","欲","彩","领","脚","脖","脸","脱","象","够",
                "猜","猪","猎","猫","猛","馅","馆","凑","减","毫","麻","痒","痕","廊","康","庸","鹿","盗","章",
                "竟","商","族","旋","望","率","着","盖","粘","粗","粒","断","剪","兽","清","添","淋","淹","渠",
                "渐","混","渔","淘","液","淡","深","婆","梁","渗","情","惜","惭","悼","惧","惕","惊","惨","惯",
                "寇","寄","宿","窑","密","谋","谎","祸","谜","逮","敢","屠","弹","随","蛋","隆","隐","婚","婶",
                "颈","绩","绪","续","骑","绳","维","绵","绸","绿","琴","斑","替","款","堪","搭","塔","越","趁",
                "趋","超","提","堤","博","揭","喜","插","揪","搜","煮","援","裁","搁","搂","搅","握","揉","斯",
                "期","欺","联","散","惹","葬","葛","董","葡","敬","葱","落","朝","辜","葵","棒","棋","植","森",
                "椅","椒","棵","棍","棉","棚","棕","惠","惑","逼","厨","厦","硬","确","雁","殖","裂","雄","暂",
                "雅","辈","悲","紫","辉","敞","赏","掌","晴","暑","最","量","喷","晶","喇","遇","喊","景","践",
                "跌","跑","遗","蛙","蛛","蜓","喝","喂","喘","喉","幅","帽","赌","赔","黑","铸","铺","链","销",
                "锁","锄","锅","锈","锋","锐","短","智","毯","鹅","剩","稍","程","稀","税","筐","等","筑","策",
                "筛","筒","答","筋","筝","傲","傅","牌","堡","集","焦","傍","储","奥","街","惩","御","循","艇",
                "舒","番","释","禽","腊","脾","腔","鲁","猾","猴","然","馋","装","蛮","就","痛","童","阔","善",
                "羡","普","粪","尊","道","曾","焰","港","湖","渣","湿","温","渴","滑","湾","渡","游","滋","溉",
                "愤","慌","惰","愧","愉","慨","割","寒","富","窜","窝","窗","遍","裕","裤","裙","谢","谣","谦",
                "属","屡","强","粥","疏","隔","隙","絮","嫂","登","缎","缓","编","骗","缘","瑞","魂","肆","摄",
                "摸","填","搏","塌","鼓","摆","携","搬","摇","搞","塘","摊","蒜","勤","鹊","蓝","墓","幕","蓬",
                "蓄","蒙","蒸","献","禁","楚","想","槐","榆","楼","概","赖","酬","感","碍","碑","碎","碰","碗",
                "碌","雷","零","雾","雹","输","督","龄","鉴","睛","睡","睬","鄙","愚","暖","盟","歇","暗","照",
                "跨","跳","跪","路","跟","遣","蛾","蜂","嗓","置","罪","罩","错","锡","锣","锤","锦","键","锯",
                "矮","辞","稠","愁","筹","签","简","毁","舅","鼠","催","傻","像","躲","微","愈","遥","腰","腥",
                "腹","腾","腿","触","解","酱","痰","廉","新","韵","意","粮","数","煎","塑","慈","煤","煌","满",
                "漠","源","滤","滥","滔","溪","溜","滚","滨","粱","滩","慎","誉","塞","谨","福","群","殿","辟",
                "障","嫌","嫁","叠","缝","缠","静","碧","璃","墙","撇","嘉","摧","截","誓","境","摘","摔","聚",
                "蔽","慕","暮","蔑","模","榴","榜","榨","歌","遭","酷","酿","酸","磁","愿","需","弊","裳","颗",
                "嗽","蜻","蜡","蝇","蜘","赚","锹","锻","舞","稳","算","箩","管","僚","鼻","魄","貌","膜","膊",
                "膀","鲜","疑","馒","裹","敲","豪","膏","遮","腐","瘦","辣","竭","端","旗","精","歉","熄","熔",
                "漆","漂","漫","滴","演","漏","慢","寨","赛","察","蜜","谱","嫩","翠","熊","凳","骡","缩","慧",
                "撕","撒","趣","趟","撑","播","撞","撤","增","聪","鞋","蕉","蔬","横","槽","樱","橡","飘","醋",
                "醉","震","霉","瞒","题","暴","瞎","影","踢","踏","踩","踪","蝶","蝴","嘱","墨","镇","靠","稻",
                "黎","稿","稼","箱","箭","篇","僵","躺","僻","德","艘","膝","膛","熟","摩","颜","毅","糊","遵",
                "潜","潮","懂","额","慰","劈","操","燕","薯","薪","薄","颠","橘","整","融","醒","餐","嘴","蹄",
                "器","赠","默","镜","赞","篮","邀","衡","膨","雕","磨","凝","辨","辩","糖","糕","燃","澡","激",
                "懒","壁","避","缴","戴","擦","鞠","藏","霜","霞","瞧","蹈","螺","穗","繁","辫","赢","糟","糠",
                "燥","臂","翼","骤","鞭","覆","蹦","镰","翻","鹰","警","攀","蹲","颤","瓣","爆","疆","壤","耀",
                "躁","嚼","嚷","籍","魔","灌","蠢","霸","露","囊","罐"};

        int surNameLen = surName.length;
        int doubleSurNameLen = doubleSurName.length;
        int wordLen = word.length;

        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        if(simple){
            sb.append(surName[random.nextInt(surNameLen)]);
            int surLen = sb.toString().length();
            for (int i = 0; i < len - surLen; i++) {
                if(sb.toString().length() <= len){
                    sb.append(word[random.nextInt(wordLen)]);
                }
            }
        }else{
            sb.append(doubleSurName[random.nextInt(doubleSurNameLen)]);
            int doubleSurLen = sb.toString().length();
            for (int i = 0; i < len - doubleSurLen; i++) {
                if(sb.toString().length() <= len){
                    sb.append(word[random.nextInt(wordLen)]);
                }
            }
        }
        return sb.toString();
    }
    public static boolean filterSearchWords(String words){
        return !filterWords(words);
    }
    public static boolean filterWords(String words){
        for (FilterWords word: filterWords) {
            if (word.getWords().contains(words)) return true;
        }
        return false;
    }
    public static boolean filterCommentBlack(String words){
        for (FilterWords word: filterWords) {
            if (word.getWords().contains(words)){
                if (word.getBlack() == 1){
                    return true;
                }
            }
        }
        return false;
    }
    public static long cardinality(long max){
        return cardinality(100,max);
    }
    public static long cardinality(long mini, long max){
        if (mini < 0) mini = 0;
        if (max < mini) max = mini+2;
        return  (long) (mini+Math.random()*(max-mini+1));
    }
    public static boolean checkEmailFormat(String content){
        String REGEX="^\\w+((-\\w+)|(\\.\\w+))*@\\w+(\\.\\w{2,3}){1,3}$";
        Pattern p = Pattern.compile(REGEX);
        Matcher matcher=p.matcher(content);

        return matcher.matches();
    }
    public static String getJsonBodyString(HttpServletRequest httpServletRequest) {
        try {
            httpServletRequest.setCharacterEncoding("UTF-8");
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = null;
            reader = new BufferedReader(new InputStreamReader(httpServletRequest.getInputStream(), StandardCharsets.UTF_8));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static  String getToken(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replaceAll("-","")+System.currentTimeMillis();
    }
    public static String getRandom(int n){
        return RandomStringUtils.randomAlphanumeric(n);
    }
    public static String getSalt(){
        return getRandom(32);
    }
    public  static boolean isNumberString(String s){
        for (int i=0;i< s.length(); i++){
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }
    public static String getWxSign(Map<String, String> map, String key) {

        String result = "";
        try {
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(map.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {

                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });

            // 构造签名键值对的格式
            List<String> values = new ArrayList<String>();
            for (Map.Entry<String, String> item : infoIds) {
                if (item.getKey() != null || item.getKey() != "") {
                    String k = item.getKey();
                    String v = item.getValue();
                    if (!(v == "" || v == null)) {
                        values.add(k + "=" + v);
                    }
                }
            }

            String sign = StringUtils.join(values, "&") + key;
            //System.out.println(sign);

            //进行MD5加密
            result = DigestUtils.md5DigestAsHex(sign.getBytes()).toUpperCase();
        } catch (Exception e) {
            return null;
        }
        return result;
    }
    public  static String doPost(String url, Map<String, String> map) throws Exception {
        HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try {
            httpClient = new DefaultHttpClient();
            httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + "UTF-8");
            RequestConfig build = RequestConfig.custom().setConnectTimeout(10000).build();
            httpPost.setConfig(build);
            //设置参数
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> elem = (Map.Entry<String, String>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
            }
            if (list.size() > 0) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
                httpPost.setEntity(entity);
            }
            HttpResponse response = httpClient.execute(httpPost);
            if (response != null) {
                org.apache.http.HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, "UTF-8");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            //throw new Exception();
        }
        return result;
    }
    public static String request(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = reader.readLine();
            if (strRead != null) {
                sbf.append(strRead);
                while ((strRead = reader.readLine()) != null) {
                    sbf.append("\n");
                    sbf.append(strRead);
                }
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public static String encodeUrlString(String str, String charset) {
        String strret = null;
        if (str == null)
            return str;
        try {
            strret = java.net.URLEncoder.encode(str, charset);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return strret;
    }
    public static String md5(String plainText) {
        StringBuffer buf = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return buf.toString();
    }
    public static String encrypt(String key, String src){
        String p = null;
//        String src = "name=Alice&text=Hello";
        byte[] aesKey = key.getBytes(StandardCharsets.UTF_8);
        try{
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(aesKey, "AES"));
            byte[] encrypted = cipher.doFinal(src.getBytes(StandardCharsets.UTF_8));
            p = Base64.getEncoder().encodeToString(encrypted);
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException |
                 InvalidKeyException e) {
            e.printStackTrace();
        }
        return p;
    }
    public static String getSign(String key, String p, int t){
        return DigestUtils.md5DigestAsHex((p + t + key).getBytes());
    }
    public static String sendGet(String httpUrl, Map<String, String> parameter) {
        if (parameter == null || httpUrl == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = parameter.entrySet().iterator();
        while (iterator.hasNext()) {
            if (sb.length() > 0) {
                sb.append('&');
            }
            Map.Entry<String, String> entry = iterator.next();
            String key = entry.getKey();
            String value;
            try {
                value = URLEncoder.encode(entry.getValue(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                value = "";
            }
            sb.append(key).append('=').append(value);
        }
        String urlStr = null;
        if (httpUrl.lastIndexOf('?') != -1) {
            urlStr = httpUrl + '&' + sb.toString();
        } else {
            urlStr = httpUrl + '?' + sb.toString();
        }

        HttpURLConnection httpCon = null;
        String responseBody = null;
        try {
            URL url = new URL(urlStr);
            httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("GET");
            httpCon.setConnectTimeout(TIME_OUT * 1000);
            httpCon.setReadTimeout(TIME_OUT * 1000);
            // 开始读取返回的内容
            InputStream in = httpCon.getInputStream();
            byte[] readByte = new byte[1024];
            // 读取返回的内容
            int readCount = in.read(readByte, 0, 1024);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while (readCount != -1) {
                baos.write(readByte, 0, readCount);
                readCount = in.read(readByte, 0, 1024);
            }
            responseBody = new String(baos.toByteArray(), "UTF-8");
            baos.close();
        } catch (Exception ignored) {
        } finally {
            if (httpCon != null)
                httpCon.disconnect();
        }
        return responseBody;
    }
}
