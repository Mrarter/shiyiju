Component({
  data: {
    selected: 0,
    color: "#8D8579",
    selectedColor: "#D6B074",
    list: [
      {
        pagePath: "/pages/home/index",
        text: "首页",
        icon: "首"
      },
      {
        pagePath: "/pages/publish/index",
        text: "发布",
        icon: "发"
      },
      {
        pagePath: "/pages/cart/index",
        text: "购物车",
        icon: "购"
      },
      {
        pagePath: "/pages/mine/index",
        text: "我的",
        icon: "我"
      }
    ]
  },
  lifetimes: {
    attached() {
      const page = getCurrentPages().pop();
      const route = `/${page.route}`;
      const selected = this.data.list.findIndex((item) => item.pagePath === route);
      this.setData({ selected: selected < 0 ? 0 : selected });
    }
  },
  methods: {
    switchTab(event) {
      const { path, index } = event.currentTarget.dataset;
      this.setData({ selected: index });
      wx.switchTab({ url: path });
    }
  }
})
