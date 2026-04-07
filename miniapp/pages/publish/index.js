Page({
  data: {
    cards: [
      { title: "艺术家发布入口", desc: "新增作品、编辑图集、配置价格", buttonText: "申请发布", action: "artist" },
      { title: "藏家转售", desc: "填写转售价、选择交付方式", buttonText: "发起转售", action: "resale" }
    ]
  },

  handleAction(event) {
    const action = event.currentTarget.dataset.action
    if (action === "artist") {
      wx.showModal({
        title: "艺术家发布",
        content: "当前先进入人工审核流程，后续会补完整发布表单。",
        showCancel: false
      })
      return
    }

    if (action === "resale") {
      wx.navigateTo({ url: "/pages/resale/create" })
    }
  }
})
