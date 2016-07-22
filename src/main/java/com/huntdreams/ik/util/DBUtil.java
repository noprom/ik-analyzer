package com.huntdreams.ik.util;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBUtil
 * 连接mysql
 * <p/>
 * Author: Noprom <tyee.noprom@qq.com>
 * Date: 7/22/16 9:59 AM.
 */
public class DBUtil {

    /**
     * 获得数据库连接
     *
     * @return
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName(CompositeFactory.getInstance().getString(Constant.DRIVER_CLASS_NAME_KEY));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection(
                    CompositeFactory.getInstance().getString(Constant.URL_KEY),
                    CompositeFactory.getInstance().getString(Constant.USERNAME_KEY),
                    CompositeFactory.getInstance().getString(Constant.PASSWORD_KEY));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 测试插入数据
     *
     * @throws SQLException
     */
    public static void insert() {
        System.out.println("-------------insert()-------------");
        //创建连接
        Connection conn = DBUtil.getConnection();
        //创建SQL执行工具
        QueryRunner qRunner = new QueryRunner();
        //执行SQL插入
        int n = 0;
        try {
            n = qRunner.update(conn, "insert into apps(package, info) values('com.android.app','{\"info\": {\"应用内商品\": \"数字商品购买\", \"安装次数\": \"10,000,000 - 50,000,000\", \"提供者：\": \"举报不当内容\", \"互动元素\": \"了解详情\", \"权限\": \"每件$0.99-$54.99\", \"Android系统版本要求\": \"2.0及更高版本\", \"开发者\": \"NAVITEL\", \"更新日期\": \"2016年7月14日\", \"内容分级\": \"3 岁以上\", \"举报\": \"查看详情\"}, \"cate\": [\"出行导航\"], \"url\": \"https://play.google.com/store/apps/details?id=com.navitel\", \"rate\": [\"3 岁以上\"], \"num\": [\" 10,000,000 - 50,000,000 \"], \"meta\": [\"离线GPS导航和免费64 countries.Try流行的导航地图的！\"], \"pkg\": \"com.navitel\", \"score\": [\"4.1\"], \"desc\": \"  Yandex.Navigator可以帮助您通过交通找到最好的路线。        免费的GPS导航 - 从OSM和TomTom的离线地图。从这里去任何地方。        超过10万的公司与迪拜和塞浦路斯3D地图公共交通路线！        全球最先进的导航应用程序，深受 1.50 亿驾驶者信赖。        一個很酷的GPS 駕駛與導航應用軟件，與其他司機聯繫        免费GPS导航和Android版离线地图的基础上QualityMaps        中国各地，美国，欧洲和全世界旅行与免费地图！轻松浏览，跟踪路线和查找旅游景点！        我最喜欢的导航生活！        下载最新版 Google 地图应用。        GPS导航和地图。在线和离线。令人难以置信的技术。对于免费的。        搜索组织，规划路线，交通及运输信息，使用离线地图        免费离线GPS导航仪TourMap交通和高速摄像机。        离线地图和导航就在这里！        现可用于Android 的最有趣导航软件。 毫无置疑。        堵车，雷达和全球的免费地图的导航仪最快        为城市，道路交通和公共交通准备的GPS导航和地图：卫星图像和街景。        NaviTag是一个免费的应用程序允许定位和跟踪物体的位置。      \"}')");
            System.out.println("successfully insert " + n + " rows of data");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //关闭数据库连接
            DbUtils.closeQuietly(conn);
        }
    }

    public static void main(String[] args) {
        DBUtil.insert();
    }
}