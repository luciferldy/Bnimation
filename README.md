# Bnimation

本项目来源于 [HenCoder「仿写酷界面」征稿 ](http://mp.weixin.qq.com/s/T5tymYD1jhvxHY8F51TV5Q)

现阶段实现了模仿即刻点赞效果以及数字跳动增长。

即刻的点赞效果

![](image/0.jpg)

![](image/0.gif)

![](image/1.gif)

下面是我实现的效果（最下为 `TextView` 自带的展示数字效果）

![](image/jike.gif)

主要使用了 `PropertyValuesHolder` 和 `ObjectAnimator` 来编写动画。点赞效果可以分为手势图标的缩小放大（1.0->0.8->1.0，符合重力按下的效果），水纹图案的扩散并逐渐透明，手势上的 'shining' 图标的放大。通过 `AnimatorSet` 控制这三种动画的播放顺序。数字跳动效果通过比较新数字与旧数字之间的差异，从左侧开始第一个不同的数字以及后面的数字都需要跳动。如当前显示为 99，99+1=100，99 与 100 从第一位开始就不同，所以需要整体滚动。而 909+1=910 这种情况，只需要从第 2 位开始滚动即可。

滚动效果的实现主要使用 

```java
class Paint {
  ...
    public void drawText(@NonNull String text, int start, int end, float x, float y,
                         @NonNull Paint paint) {
      super.drawText(text, start, end, x, y, paint);
    }
  
}

```

这个函数。坐标 `y` 根据 `view` 的高度与字体的 `ascent` 和 `descent` 属性可以得到纵向中间的位置。坐标 `x` 使用 `Paint` 类的 `getTextWidths()` 获得每一位数字的宽度，每次累加即可（注意，0~9 每个数字的宽度不完全相等）。

图标来源于[即刻 App](https://www.ruguoapp.com/) 反编译后的资源包，布局也使用了即刻的布局（还是有些复杂的）

![](image/jike-layout.png)