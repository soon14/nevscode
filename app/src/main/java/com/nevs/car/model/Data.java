package com.nevs.car.model;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/30.
 * {"isSuccess":"Y","reason":"","data":[{"newsID":20,"title":"新闻15","releaseDate":"2018-04-19"},
 * {"newsID":21,"title":"新闻16","releaseDate":"2018-04-19"},
 * {"newsID":22,"title":"新闻17","releaseDate":"2018-04-19"},
 * {"newsID":23,"title":"新闻18","releaseDate":"2018-04-19"},
 * {"newsID":24,"title":"新闻19","releaseDate":"2018-04-19"}]}
 */
public class Data {
    public static String[] MAIN_TITLE2 = {"行程一", "行程二", "行程三", "行程四", "行程五"};
    public static String[] MAIN_TITLE = {"新闻15", "新闻16", "新闻17", "新闻18", "状态19"};
    public static String[] MAIN_INFO = {"2018-04-19","2018-04-19","2018-04-19","2018-04-19","2018-04-19"};
    public static String[] MAIN_IMAGE_URL = {"http://img3.imgtn.bdimg.com/it/u=2025237512,1968900013&fm=21&gp=0.jpg", "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=3229897698,2043497950&fm=21&gp=0.jpg", "http://e.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=9b8f4fa4da33c895a62b907fe4235fc6/0823dd54564e925845a2bedd9f82d158ccbf4e6a.jpg",
            "http://f.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=f6a651714c4a20a4314b34c3a562b414/a50f4bfbfbedab64b6ff44a3f436afc378311ec7.jpg",
            "http://d.hiphotos.baidu.com/zhidao/wh%3D600%2C800/sign=27d514408cb1cb133e3c3415ed647a76/b7003af33a87e95026578cc311385343faf2b4d8.jpg",
          };
    public static String CHAT_DATA_TEXT = "欢迎您下载Android快速开发框架Demo,框架持续更新中,有兴趣的同学可以在GITHUB和我一起完善这个项目,如果您有什么更好的想法和意见您也可以与我联系!!!!!!!!!!";
    public static String CHAT_DATA_URL = "http://d.hiphotos.baidu.com/zhidao/wh%3D600%2C800/sign=27d514408cb1cb133e3c3415ed647a76/b7003af33a87e95026578cc311385343faf2b4d8.jpg";


    public static List<MainDateDto> getData() {
        List<MainDateDto> data = new ArrayList<MainDateDto>();
        for (int i = 0; i < MAIN_TITLE.length; i++) {
            data.add(new MainDateDto(MAIN_TITLE[i], MAIN_INFO[i], MAIN_IMAGE_URL[i]));
        }
        return data;
    }

    public static List<ChatDto> getMultipleItemData() {
        List<ChatDto> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            if (i % 2 == 0) {
                ChatDto multipleItem = new ChatDto(CHAT_DATA_TEXT, "");
                multipleItem.setItemType(ChatDto.TEXT);
                list.add(multipleItem);
            } else if (i % 3 == 0) {
                ChatDto multipleItem = new ChatDto("", MAIN_IMAGE_URL[0]);
                multipleItem.setItemType(ChatDto.IMG);
                list.add(multipleItem);
            } else {
                ChatDto multipleItem = new ChatDto(CHAT_DATA_TEXT, MAIN_IMAGE_URL[0]);
                multipleItem.setItemType(ChatDto.IMGS);
                list.add(multipleItem);
            }
        }
        return list;
    }

    public static String[] CAR_IMAGE = {"http://img3.imgtn.bdimg.com/it/u=2025237512,1968900013&fm=21&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=2025237512,1968900013&fm=21&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=2025237512,1968900013&fm=21&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=2025237512,1968900013&fm=21&gp=0.jpg"};
    public static String[] CONTENT = {"我的车辆","授权车辆","授权车辆","授权车辆"};
    public static String[] CAR_TYPE = {"NEVS-9-3 EV","NEVS-9-3 EV","NEVS-9-3 EV","NEVS-9-3 EV"};
    public static String[] CAR_NUMBER = {"津A88888","津A88888","津A88888","津A88888"};
    public static List<ChooseCarItem> getChooseCar() {
        List<ChooseCarItem> data = new ArrayList<>();
        for (int i = 0; i < CAR_TYPE.length; i++) {
            data.add(new ChooseCarItem(CAR_IMAGE[i],CONTENT[i], CAR_TYPE[i], CAR_NUMBER[i]));
        }
        return data;
    }

    public static String[] CAR_IMAGEMY = {"http://img3.imgtn.bdimg.com/it/u=2025237512,1968900013&fm=21&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=2025237512,1968900013&fm=21&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=2025237512,1968900013&fm=21&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=2025237512,1968900013&fm=21&gp=0.jpg"};
    public static String[] CONTENTMY = {"已认证","已认证","已认证","已认证"};
    public static String[] CAR_TYPEMY = {"NEVS-9-3 EV","NEVS-9-3 EV","NEVS-9-3 EV","NEVS-9-3 EV"};
    public static String[] CAR_NUMBERMY = {"车辆授权","车辆授权","车辆授权","车辆授权"};
    public static List<MyCarItem> getMyCar() {
        List<MyCarItem> data = new ArrayList<>();
        for (int i = 0; i < CAR_TYPEMY.length; i++) {
            data.add(new MyCarItem(CAR_IMAGEMY[i],CONTENTMY[i], CAR_TYPEMY[i], CAR_NUMBERMY[i]));
        }
        return data;
    }




    public static List<MainDateDto> getData2() {
        List<MainDateDto> data = new ArrayList<MainDateDto>();
        for (int i = 0; i < MAIN_TITLE2.length; i++) {
            data.add(new MainDateDto(MAIN_TITLE2[i], MAIN_INFO[i], MAIN_IMAGE_URL[i]));
        }
        return data;
    }
}
