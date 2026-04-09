Component({
  data: {
    selected: 0,
    color: "#999999",
    selectedColor: "#000000",
    list: [
      {
        pagePath: "/pages/home/index",
        text: "首页",
        icon: "🏠"
      },
      {
        pagePath: "/pages/agent/index",
        text: "经纪人",
        icon: "🤝"
      },
      {
        pagePath: "/pages/cart/index",
        text: "购物车",
        icon: "🛒"
      },
      {
        pagePath: "/pages/mine/index",
        text: "我的",
        icon: "👤"
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
