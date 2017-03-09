package com.youyouza.pedmeter;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.youyouza.Kmeans.Kmeans;
import com.youyouza.LRClass.LRclass;

import java.util.Arrays;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }


    public void stepCount(double[] data) {

//        获取聚类中心
        double[] centerData = Kmeans.getKmeanCenter(data, 3, 500);
//        从小到大排序
        Arrays.sort(centerData);

        double temp1 = centerData[2] - centerData[1];

        double topLevel = 0.0;

//        进行分类 以及对不同类别进行不同阈值的选取
        int type = LRclass.getClass(centerData);
        if (type == 1) return;  //无用的数据
        else if (type == 2) topLevel = centerData[1] + temp1 / 2;
        else if (type == 3 || type == 4) topLevel = centerData[1] + temp1 / 4;
        else if (type == 5) topLevel = centerData[1];

        double topIndex = 0; //波峰下标
        for (int ite = 1; ite <= data.length - 2; ++ite) {

            // 不满足阈值要求 或者不满足波峰要求
            if ((data[ite] < topLevel) || (data[ite] <= data[ite + 1]) || (data[ite] <= data[ite - 1]))
                continue;
//             满足阈值和波峰要求 满足滑动窗口要求
            if ((topIndex == 0) || ((ite - topIndex >= 5) && (ite - topIndex < 34))) {
                addCount(1, type);     //步数加1
                topIndex = ite;      //更新当前波峰下标
            } else {
                if (ite - topIndex >= 34)
                    topIndex = ite;
            }
        }
    }

//       zhege


    public void addCount(int i, int type) {
    }


    private static final int avgNumber = 4;

    double xDirec[] = new double[avgNumber];

    boolean isFull = false;

    int DirecIndex = 0;

    double sumXDirec = 0.0;

    private void addOfDirec(double x) {

        if (isFull) {
            sumXDirec -= xDirec[DirecIndex];
        }
        xDirec[DirecIndex] = x;

        sumXDirec += xDirec[DirecIndex];

        DirecIndex++;

        if (DirecIndex == avgNumber) isFull = true;

        DirecIndex = DirecIndex % avgNumber;
    }


    public static double[] getKmeanCenter(double[] data, int ClusterClassNumber, int iteration) {


        double[] center = new double[ClusterClassNumber];
        double minCost = 0.0;


//        调用多次,选取距离总和最小的那次作为最终结果

        for (int index = 0; index < 5; ++index) {

            double[] tempCenter = Kmeans.getKmeans(data, ClusterClassNumber, iteration);

            if (index == 0 || tempCenter[ClusterClassNumber] < minCost) {
                minCost = tempCenter[ClusterClassNumber];
                System.arraycopy(tempCenter, 0, center, 0, ClusterClassNumber);

            }
        }
        return center;

    }

    public static double[] getKmeans(double[] data, int ClusterClassNumber, int iteration) {
        double[] ClusterCenter;
        // 随机初始化聚类中心
        double[] beforeCenter = Kmeans.initClusterCenter(data, ClusterClassNumber);
        ClusterCenter = beforeCenter;
        int count = 0;
        for (int ite = 0; ite < iteration; ++ite) {

            //遍历数据，进行新聚类中心查找，并且获取新的聚类中心

            ClusterCenter = Kmeans.findClosestClusterCenter(data, ClusterCenter, ClusterClassNumber);
            // 计算新旧两次中心距离
            double errorNow = 0.0;
            for (int index = 0; index < ClusterClassNumber; ++index) {
                double temperor = ClusterCenter[index] - beforeCenter[index];
                errorNow += temperor * temperor;
            }
            if (errorNow < 0.00002) {
                ++count;
            } else {
                count = 0;
            }
            // 满足本次聚类结束条件，迭代结束

            if (count >= 5)
                break;
        }
        return ClusterCenter;
    }


    static final int ClassType = 5;
    static final int ThetaLength = 4;

    private static double[][] Theta = {
            {72.30012, 4.89103, -2.50389, -8.97478},
            {46.84104, -5.04412, 3.12906, -3.18320},
            {36.90438, -0.87683, -6.05078, 2.67253},
            {-9.74464, 1.73633, -3.76342, 2.62070},
            {-62.17537, -0.78368, 3.42120, 1.70898}
    };


    public static int getClass(double[] CountCenter) {

        int BiggestIndex = 0;  //概率最大的类的下标
        double BiggestProbality = 0.0;  //最大的概率

        for (int classIndex = 0; classIndex < ClassType; ++classIndex) {

            double tempProbality = 0;

            for (int thetaIndex = 1; thetaIndex < ThetaLength; ++thetaIndex)

                tempProbality += Theta[classIndex][thetaIndex] * CountCenter[thetaIndex - 1];

            tempProbality += Theta[classIndex][0];

            tempProbality = LRclass.sigmoid(tempProbality);

            if (classIndex == 0)
                BiggestProbality = tempProbality;
            else {

                //获取概率最大的那个类别

                if (tempProbality >= BiggestProbality) {
                    BiggestProbality = tempProbality;
                    BiggestIndex = classIndex;
                }
            }
        }
        return BiggestIndex + 1;
    }

}