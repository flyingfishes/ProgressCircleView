## `水波纹进度条[自定义控件类]`[ProgressCircleView.java]
####使用方法，类的头部有说明
####这里我们来讲一下关键代码，对于这个控件，难就难在如何绘制水波纹这一点上，当然如果你有接触过贝塞尔曲线，相信这一点点难度立马就被你克服了。
这里我贴一下我们需要的贝塞尔曲线是怎么绘制的：<br>
![quad](http://mmbiz.qpic.cn/mmbiz_gif/FoiciaVBBCfia5rMBozTalqKT1lsnIyfCg3V8KiaqickvgicB1Bz7I3rxVQzCq3zoNOaYR5kPFicWS2FH1B4LkwO5WC0w/0?wx_fmt=gif&tp=webp&wxfrom=5&wx_lazy=1)<br>
使用方法，二阶贝塞尔曲线绘制：path.rQuadTo(dx1, dy1, dx2, dy2);参数对应P1和P2的坐标。
* 为了方便各位更好的理解，我这里分别从左边和右边绘制了水波纹曲线的效果，不过视觉上似乎没多大区别
```java
    /**
     * @param direction 0从左至右，1从右至左(绘制)
     */
    private void drawQuad(int direction) {
        //绘制进度波纹
        path.reset();
        //计算画笔所在的Y坐标值，直径 - 进度移动距离
        float py = (1 - (float) currentProgress / maxProgress) * 2 * radius;
        switch (direction) {
            case 0:
                //x轴不变，向Y轴方向移动画笔（这里为向上）
                path.moveTo(0, py);
                //默认水波纹半径
                float pRadius = 2f * radius / progressDensity;//progressDensity为水波纹的密度
                //水波纹当前半径
                float cRadius = (1 - (float) currentProgress / maxProgress) * pRadius;
                for (int i = 0; i < progressDensity; i++) {
                    //这里是在一条直线上绘制的是上下循环的贝塞尔曲线
                    //下曲线，这里可以去掉,但不去掉会更美观
                    path.rQuadTo(pRadius, cRadius, 2 * pRadius, 0);//绘制贝塞尔曲线，每次绘制相对上一条的位置开始
                    //上曲线
                    path.rQuadTo(pRadius, -cRadius, 2 * pRadius, 0);
                }
                path.lineTo(width, py);
                path.lineTo(width, heigth);
                path.lineTo(0, heigth);
                break;
            case 1:
                //x轴不变，向Y轴方向移动画笔（这里为向上）
                path.moveTo(width, py);
                //默认水波纹半径
                float pRadius1 = 2f * radius / progressDensity;//progressDensity为水波纹的密度
                //水波纹当前半径
                float cRadius1 = (1 - (float) currentProgress / maxProgress) * pRadius1;
                for (int i = 0; i < progressDensity; i++) {
                    //这里是在一条直线上绘制的是上下循环的贝塞尔曲线
                    //下曲线，这里可以去掉,但不去掉会更美观
                    path.rQuadTo(-pRadius1, cRadius1, -2 * pRadius1, 0);//绘制贝塞尔曲线，每次绘制相对上一条的位置开始
                    //上曲线
                    path.rQuadTo(-pRadius1, -cRadius1, -2 * pRadius1, 0);
                }
                path.lineTo(0, heigth);
                path.lineTo(width, heigth);
                path.lineTo(width, py);
                break;
            default:
                break;
        }
        path.close();
        bitmapCanvas.drawPath(path, progressPaint);
    }
```
###接下来就到了一言不合就贴图的时候了：
> ![image](http://img.blog.csdn.net/20161007141851251?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)
[ProgressCircleView.java]: https://github.com/flyingfishes/ProgressCircleView/edit/master/



