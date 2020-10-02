kz_uco_tsadv_web_toolkit_ui_rcgshopcomponent_RcgShopComponent = function () {
    var connector = this,
        element = connector.getElement(),
        state = connector.getState(),
        currentPage = 0,
        recordsCount = 0,
        pagesCount = 0,
        accessToken = state.authorizationToken,
        loadingData = false,
        webAppUrl = state.webAppUrl,
        goodsCartCount = Number(state.goodsCartCount),
        pageName = state.pageName,
        tabId = null,
        loadingFunction = null,
        balance = Number(state.balance),
        totalSum = Number(state.totalSum),
        language = state.language,
        goodsOrdersCount = state.goodsOrdersCount,
        heartAwardDiscount = state.heartAwardDiscount,
        heartAward = state.heartAward,
        hasDiscount = (heartAward === 1 && heartAwardDiscount !== 0),
        messageBundle = JSON.parse(state.messageBundle);

    var $win = $('.rcg-content-wrapper');

    connector.onStateChange = function () {
        //TODO:
    };

    connector.removeGoodsCartInForm = function (goodsId) {
        var findGc = $(".rcg-gc-list").find(".rcg-sh-gc-w[goods-cart='" + goodsId + "']");
        if (findGc) findGc.remove();

        $win.trigger('calculateTotalSumEvent');

        goodsCartCount--;
        $win.trigger('refreshGoodsCartCountEvent');
    };

    connector.rePaintGoodsOperation = function (goods) {
        var event = $.Event("rePaintGoodsOperationEvent");
        event.goods = goods;
        $win.trigger(event);
    };

    connector.rePaintGoodsWishList = function (goods) {
        var event = $.Event("rePaintGoodsWishListEvent");
        event.goods = goods;
        $win.trigger(event);
    };

    $win.unbind('scroll');
    $win.bind('scroll', function () {
        if (!loadingData && currentPage < pagesCount) {
            var currY = $(this).scrollTop();
            var postHeight = $(this).height();
            var scrollHeight = $('.rcg-shop-widget').height();
            var scrollPercent = (currY / (scrollHeight - postHeight)) * 100;
            if (scrollPercent > 85) {
                if (loadingFunction) {
                    loadingData = true;
                    loadingFunction.apply();
                }
            }
        }
    });

    initPage();

    function cartGoodsCountForm($num, $form_for_0, $form_for_1, $form_for_2, $form_for_5) {
        var msgCode = $form_for_0;
        $num = Math.abs($num) % 100;
        var $num_x = $num % 10;
        if ($num > 10 && $num < 20) {
            msgCode = $form_for_5;
        }
        if ($num_x > 1 && $num_x < 5) {
            msgCode = $form_for_2;
        }
        if ($num_x === 1) {
            msgCode = $form_for_1;
        }
        if ($num_x === 0) {
            return messageBundle[msgCode];
        }
        return $num + ' ' + messageBundle[msgCode];
    }

    function initPage() {
        if (tabId === null) tabId = 1;
        if (pageName === 'cart') tabId = 3;

        var $rcgCart = $('<span/>', {
            class: 'rcg-sh-h-cart',
            'goods-cart-count': goodsCartCount,
            text: cartGoodsCountForm(goodsCartCount, 'shop.cart.empty', 'shop.cart.count.1', 'shop.cart.count.2', 'shop.cart.count.3'),
            click: function () {
                if (pageName !== 'cart') {
                    var gCount = Number($(this).attr('goods-cart-count'));
                    if (gCount > 0) {
                        connector.openCart();
                    }
                }
            },
            prepend: $('<i/>', {
                class: 'fa fa-shopping-cart'
            })
        });

        $win.unbind('refreshGoodsCartCountEvent');
        $win.bind('refreshGoodsCartCountEvent', function () {
            $rcgCart.attr('goods-cart-count', goodsCartCount);
            $rcgCart.get(0).lastChild.nodeValue = cartGoodsCountForm(goodsCartCount, 'shop.cart.empty', 'shop.cart.count.1', 'shop.cart.count.2', 'shop.cart.count.3');
        });

        var $contentBody = $('<div/>', {
            class: 'rcg-sh-b'
        });

        initTemplate();

        if (pageName === 'shop') {
            openTab();

            function initCatalog() {
                var $categoryMenu = $('<ul/>', {
                    class: 'rcg-sh-menu'
                });

                var displayType = 1,
                    goodsCategory = '-1',
                    goodsSort = '2';

                var $sortSelect = $('<select/>', {
                    class: 'rcg-sh-sorts',
                    append: $('<option/>', {
                        value: '0',
                        text: messageBundle['shop.goods.sort.0']
                    }).add($('<option/>', {
                        value: '1',
                        text: messageBundle['shop.goods.sort.1']
                    })).add($('<option/>', {
                        value: '2',
                        text: messageBundle['shop.goods.sort.2']
                    }))
                });

                var $contentHeader = $('<div/>', {
                    class: 'rcg-sh-b-h row',
                    append: $('<div/>', {
                        class: 'col-lg-6 rcg-reset-col-l',
                        append: $('<div/>', {
                            class: 'rcg-sh-sort-w',
                            text: messageBundle['shop.goods.sort.by'],
                            append: function () {
                                $(this).append($sortSelect);
                                $sortSelect.val(2);
                                $sortSelect.on('change', function () {
                                    var selectedSort = $(this).val();
                                    if (selectedSort !== goodsSort) {
                                        if (!loadingData) {
                                            currentPage = recordsCount = pagesCount = 0;
                                            goodsSort = selectedSort;

                                            loadGoods();
                                        }
                                    }
                                });


                            }
                        })
                    }).add($('<div/>', {
                        class: 'col-lg-6 rcg-align-r',
                        append: $('<span/>', {
                            class: 'rcg-sh-display-type',
                            'active-tab': 1,
                            append: $('<i/>', {
                                class: 'fa fa-th-large',
                                click: function () {
                                    changeDisplayType(1, this);
                                }
                            }).add($('<i/>', {
                                class: 'fa fa-th-list',
                                click: function () {
                                    changeDisplayType(2, this);
                                }
                            }))
                        })
                    }))
                });

                var $goodsList = $('<div/>', {
                    class: 'row rcg-goods-list'
                });

                var $goodsListWrapper = $('<div/>', {
                        class: 'rcg-sh-b-w',
                        append: $contentHeader
                            .add($goodsList)
                    }),
                    $goodsLoading = $('<div/>', {
                        class: 'rcg-sh-b-w-loading'
                    });

                function changeDisplayType(dt, el) {
                    initRePaintGoodsOperation();
                    initRePaintGoodsWishList();
                    $(el).parent().attr('active-tab', dt);

                    $goodsList.empty();
                    displayType = dt;
                    currentPage = recordsCount = pagesCount = 0;
                    loadGoods();
                }

                function initRePaintGoodsOperation() {
                    $win.unbind('rePaintGoodsOperationEvent');
                    $win.bind('rePaintGoodsOperationEvent', function (e) {
                        var $card, goods = $.parseJSON(e.goods);

                        if (displayType === 1) {
                            $card = $(".rcg-sh-g-w[goods-id='" + goods.id + "']");
                        } else {
                            $card = $(".rcg-sh-g2-w[goods-id='" + goods.id + "']");
                        }

                        if ($card) {
                            $card.unbind('click');
                            $card.bind('click', function () {
                                connector.showGoodsCard(JSON.stringify(goods));
                            });

                            var $operation = $card.find('.rcg-sh-g-add-link');
                            if ($operation) {
                                $operation.replaceWith($('<a/>', {
                                    class: 'rcg-sh-g-add-link rcg-btn rcg-white-btn rcg-btn-md',
                                    text: messageBundle['goods.in.cart'],
                                    click: function (e) {
                                        e.stopImmediatePropagation();
                                        connector.openCart();
                                    }
                                }));

                                goodsCartCount++;
                                $win.trigger('refreshGoodsCartCountEvent');
                            }
                        }
                    });
                }

                function initRePaintGoodsWishList() {
                    $win.unbind('rePaintGoodsWishListEvent');
                    $win.bind('rePaintGoodsWishListEvent', function (e) {
                        var $card, goods = $.parseJSON(e.goods);

                        if (displayType === 1) {
                            $card = $(".rcg-sh-g-w[goods-id='" + goods.id + "']");
                        } else {
                            $card = $(".rcg-sh-g2-w[goods-id='" + goods.id + "']");
                        }

                        if ($card) {
                            var $link = $card.find('.rcg-sh-g-add-wl');
                            if ($link) {
                                var inWishList = goods.inWishList,
                                    $icon = $($link).find('i');

                                $link.attr('liked', inWishList);
                                $icon.removeClass('fa-heart fa-heart-o');
                                if (inWishList) {
                                    $icon.addClass('fa-heart');
                                } else {
                                    $icon.addClass('fa-heart-o');
                                }
                            }
                        }
                    });
                }

                $('<div/>', {
                    class: 'row',
                    append: $('<div/>', {
                        class: 'col-lg-3',
                        append: $('<div/>', {
                            class: 'rcg-sh-menu-w',
                            append: $categoryMenu
                        })
                    }).add($('<div/>', {
                        class: 'col-lg-9',
                        append: $goodsListWrapper
                    }))
                }).appendTo($contentBody);

                initRePaintGoodsOperation();
                initRePaintGoodsWishList();
                loadMenu();
                loadGoods();

                loadingFunction = function () {
                    loadGoods();
                };

                function loadGoods() {
                    var init = currentPage === 0;
                    currentPage++;

                    $.ajax({
                        'url': webAppUrl + "/rest/v2/services/tsadv_RecognitionService/loadGoods",
                        data: {
                            page: currentPage,
                            lastCount: recordsCount,
                            categoryId: goodsCategory,
                            sort: goodsSort
                        },
                        'dataType': 'json',
                        'beforeSend': function (request) {
                            $goodsListWrapper.append($goodsLoading).addClass('rcg-sh-b-w-d');

                            request.setRequestHeader("Authorization", "Bearer " + accessToken);
                            request.setRequestHeader("Accept-Language", language);

                            if (init) {
                                $goodsList.empty();
                            }
                        }
                    }).done(function (data, textStatus, jqXHR) {
                        if (data) {
                            pagesCount = Number(data.pageInfo.pagesCount);
                            recordsCount = Number(data.pageInfo.totalRowsCount);

                            var goods = data.goods;
                            if (recordsCount > 0 && (goods && goods.length > 0)) {
                                $.each(goods, function (i, item) {
                                    $goodsList.append(makeGoods(item));
                                });
                            } else {
                                if (init) {
                                    $goodsList.append(emptyBlock(messageBundle['there.nothing']));
                                }
                            }

                            initTooltip();
                        }
                    }).fail(function (jqXHR, textStatus, error) {
                        $goodsList.append(emptyBlock(jqXHR.responseText));
                    }).always(function () {
                        loadingData = false;

                        $goodsListWrapper.removeClass('rcg-sh-b-w-d');
                        $goodsLoading.detach();
                    });

                    function addToCart(el, goods) {
                        var data = {goodsId: goods.id};

                        $.ajax({
                            url: webAppUrl + "/rest/v2/services/tsadv_RecognitionService/addGoodsToCart",
                            type: "POST",
                            contentType: "application/json",
                            data: JSON.stringify(data),
                            headers: {
                                "Authorization": "Bearer " + accessToken,
                                "Accept-Language": language
                            },
                            beforeSend: function (request) {
                                $(el).addClass('rcg-btn-loading')
                                //show loader
                            }
                        }).done(function (data, textStatus, jqXHR) {
                            $(el).replaceWith($('<a/>', {
                                class: 'rcg-sh-g-add-link rcg-btn rcg-white-btn rcg-btn-md',
                                text: messageBundle['goods.in.cart'],
                                click: function (e) {
                                    e.stopImmediatePropagation();
                                    connector.openCart();
                                }
                            }));
                            goods.inCart = 1;

                            goodsCartCount++;
                            $win.trigger('refreshGoodsCartCountEvent');
                        }).fail(function (jqXHR, textStatus, error) {
                            alert('fail:' + jqXHR.responseText);
                        }).always(function (res) {
                            $(el).removeClass('rcg-btn-loading');
                        })
                    }

                    function addToWishList(el, goods) {
                        var data = {goodsId: goods.id};

                        $.ajax({
                            url: webAppUrl + "/rest/v2/services/tsadv_RecognitionService/addToWishList",
                            type: "POST",
                            contentType: "application/json",
                            data: JSON.stringify(data),
                            headers: {
                                "Authorization": "Bearer " + accessToken,
                                "Accept-Language": language
                            },
                            beforeSend: function (request) {
                                $(el).addClass('rcg-btn-loading')
                                //show loader
                            }
                        }).done(function (data, textStatus, jqXHR) {
                            var inWishList = Number(Math.abs($(el).attr('liked') - 1)),
                                $icon = $(el).find('i');

                            $icon.removeClass('fa-heart fa-heart-o');
                            if (inWishList === 0) {
                                $icon.addClass('fa-heart-o');
                            } else {
                                $icon.addClass('fa-heart');
                            }
                            $(el).attr('liked', inWishList);
                            goods.inWishList = inWishList;
                        }).fail(function (jqXHR, textStatus, error) {
                            alert('fail:' + jqXHR.responseText);
                        }).always(function (res) {
                            $(el).removeClass('rcg-btn-loading');
                        })
                    }

                    function makeGoods(goods) {
                        var $operation, inWishList = goods.inWishList;
                        if (goods.inCart === 0) {
                            if (goods.quantity === 0) {
                                $operation = $('<a/>', {
                                    class: 'rcg-sh-g-add-link rcg-btn rcg-white-btn rcg-btn-md',
                                    text: messageBundle['goods.empty']
                                });
                            } else {
                                $operation = $('<a/>', {
                                    class: 'rcg-sh-g-add-link rcg-btn rcg-blue-btn rcg-btn-md',
                                    text: messageBundle['add.to.cart'],
                                    click: function (e) {
                                        e.stopImmediatePropagation();
                                        addToCart(this, goods);
                                    }
                                });
                            }
                        } else {
                            $operation = $('<a/>', {
                                class: 'rcg-sh-g-add-link rcg-btn rcg-white-btn rcg-btn-md',
                                text: messageBundle['goods.in.cart'],
                                click: function (e) {
                                    e.stopImmediatePropagation();
                                    connector.openCart();
                                }
                            });
                        }

                        if (displayType === 1) {
                            return $('<div/>', {
                                class: 'col-lg-4 rcg-reset-col-l',
                                append: $('<div/>', {
                                    class: 'rcg-sh-g-w',
                                    'goods-id': goods.id,
                                    click: function () {
                                        connector.showGoodsCard(JSON.stringify(goods));
                                    },
                                    append: $('<img/>', {
                                        class: 'rcg-sh-g-image',
                                        src: goods.image
                                    }).add($('<div/>', {
                                        class: 'rcg-sh-g-name',
                                        text: goods.name
                                    })).add($('<div/>', {
                                        class: 'rcg-sh-g-price-w',
                                        append: $('<i/>', {
                                            class: 'rcg-sh-g-price-icon'
                                        }).add($('<span/>', {
                                            class: 'rcg-sh-g-price',
                                            text: goods.price
                                        })).add($('<span/>', {
                                            text: 'HC'
                                        }))
                                    })).add($('<div/>', {
                                        class: 'rcg-sh-g-add-w',
                                        append: $('<a/>', {
                                            'liked': inWishList,
                                            class: 'rcg-sh-g-add-wl rcg-btn rcg-white-btn rcg-btn-md',
                                            append: $('<i/>', {
                                                class: 'fa fa-heart' + (inWishList === 0 ? '-o' : '')
                                            }),
                                            click: function (e) {
                                                e.stopImmediatePropagation();
                                                addToWishList(this, goods);
                                            }
                                        }).add($operation)
                                    }))
                                })
                            });
                        } else {
                            return $('<div/>', {
                                class: 'col-lg-12 rcg-reset-col-l',
                                append: $('<div/>', {
                                    class: 'row rcg-sh-g2-w',
                                    'goods-id': goods.id,
                                    append: $('<div/>', {
                                        class: 'col-lg-3 rcg-sh-g2-image-w',
                                        append: $('<img/>', {
                                            class: 'rcg-sh-g2-image',
                                            src: goods.image,
                                            click: function () {
                                                connector.showGoodsCard(JSON.stringify(goods));
                                            }
                                        })
                                    }).add($('<div/>', {
                                        class: 'col-lg-6 rcg-sh-g2-body',
                                        append: $('<div/>', {
                                            class: 'rcg-sh-g2-name',
                                            text: goods.name
                                        }).add($('<div/>', {
                                            class: 'rcg-sh-g2-description',
                                            text: goods.description
                                        }))
                                    })).add($('<div/>', {
                                        class: 'col-lg-3 rcg-sh-g2-pw',
                                        append: $('<div/>', {
                                            class: 'rcg-sh-g2-price',
                                            text: goods.price,
                                            append: $('<span/>', {
                                                text: ' HC'
                                            }),
                                            prepend: $('<span/>')
                                        }).add($('<div/>', {
                                            class: 'rcg-sh-g2-add-w',
                                            append: $('<a/>', {
                                                'liked': inWishList,
                                                class: 'rcg-sh-g-add-wl rcg-btn rcg-white-btn rcg-btn-md',
                                                append: $('<i/>', {
                                                    class: 'fa fa-heart' + (inWishList === 0 ? '-o' : '')
                                                }),
                                                click: function (e) {
                                                    e.stopImmediatePropagation();
                                                    addToWishList(this, goods);
                                                }
                                            }).add($operation)
                                        }))
                                    }))
                                })
                            });
                        }
                    }

                    function emptyBlock(message) {
                        var $block;
                        if (displayType === 1) {
                            $block = $('<div/>', {
                                class: 'col-lg-12',
                                append: $('<div/>', {
                                    class: 'rcg-team-empty',
                                    text: message
                                })
                            })
                        } else {
                            $block = $('<tr/>', {
                                append: $('<td/>', {
                                    class: 'rcg-team-empty',
                                    colspan: 5,
                                    text: message
                                })
                            })
                        }
                        return $block;
                    }
                }

                function loadMenu() {
                    $.ajax({
                        'url': webAppUrl + "/rest/v2/services/tsadv_RecognitionService/loadCategories",
                        'dataType': 'json',
                        'beforeSend': function (request) {
                            $categoryMenu.addClass('rcg-team-wrapper-d');

                            request.setRequestHeader("Authorization", "Bearer " + accessToken);
                            request.setRequestHeader("Accept-Language", language);
                        }
                    }).done(function (data, textStatus, jqXHR) {
                        if (data && data.length > 0) {
                            $.each(data, function (k, item) {
                                makeMenuItem($categoryMenu, item);
                            });
                        }
                    }).fail(function (jqXHR, textStatus, error) {
                        $categoryMenu.append(emptyBlock(jqXHR.responseText));
                    }).always(function () {

                    });

                    function makeMenuItem($root, node) {
                        var $li = $('<li/>', {
                            append: $('<a/>', {
                                text: node.name,
                                append: $('<i/>', {
                                    text: node.goodsCount
                                }),
                                click: function () {
                                    $root.find('li').removeClass('sh-menu-active');
                                    $(this).parent().addClass('sh-menu-active');

                                    if (node.main === 1) {
                                        return false;
                                    }

                                    if (node.all === 1) {
                                        goodsCategory = '-1';
                                    } else {
                                        goodsCategory = node.categoryId;
                                    }

                                    currentPage = recordsCount = pagesCount = 0;
                                    loadGoods();
                                }
                            })
                        });

                        if (node.all === 1) {
                            $li.addClass('sh-menu-active');
                        }
                        $root.append($li);
                        if (node.children && node.children.length > 0) {
                            var $ul = $('<ul/>', {
                                class: 'rcg-sh-menu'
                            });
                            $li.append($ul);
                            $.each(node.children, function (k, v) {
                                makeMenuItem($ul, v);
                            });
                        }
                    }
                }
            }

            function initOrders() {
                var $goTableBody = $('<tbody/>'),
                    $goTable = $('<table/>', {
                        class: 'rcg-sh-go-table',
                        append: $('<thead/>', {
                            append: $('<tr/>', {
                                append: $('<td/>', {
                                    text: messageBundle['order.td.number']
                                }).add($('<td/>', {
                                    text: messageBundle['order.td.date']
                                })).add($('<td/>', {
                                    text: messageBundle['order.td.sum']
                                })).add($('<td/>', {
                                    text: messageBundle['order.td.status']
                                }))
                            })
                        }).add($goTableBody)
                    }),
                    $loadMore = $('<div/>', {
                        class: 'rcg-sh-go-lm-w',
                        append: $('<a/>', {
                            class: 'rcg-sh-go-lm rcg-btn rcg-white-btn rcg-btn-sm',
                            href: '#',
                            text: messageBundle['load.more'],
                            click: function () {
                                loadingData = true;
                                loadGoodsOrders();
                            }
                        })
                    });

                var $goWrapper = $('<div/>', {
                        class: 'rcg-sh-go-w',
                        append: $goTable.add($loadMore)
                    }),
                    $goLoading = $('<div/>', {
                        class: 'rcg-sh-go-loading'
                    });
                $contentBody.append($goWrapper);

                loadGoodsOrders();

                loadingFunction = function () {
                    loadGoodsOrders();
                };

                function loadGoodsOrders() {
                    var init = currentPage === 0;
                    currentPage++;

                    $.ajax({
                        'url': webAppUrl + "/rest/v2/services/tsadv_RecognitionService/loadGoodsOrders",
                        data: {
                            page: currentPage,
                            lastCount: recordsCount
                        },
                        'dataType': 'json',
                        'beforeSend': function (request) {
                            $goWrapper.append($goLoading);

                            request.setRequestHeader("Authorization", "Bearer " + accessToken);
                            request.setRequestHeader("Accept-Language", language);

                            if (init) $goTableBody.empty();
                            $loadMore.hide();
                        }
                    }).done(function (data, textStatus, jqXHR) {
                        if (data) {
                            pagesCount = Number(data.pageInfo.pagesCount);
                            recordsCount = Number(data.pageInfo.totalRowsCount);

                            var orders = data.goodsOrders;
                            if (recordsCount > 0 && (orders && orders.length > 0)) {
                                $.each(orders, function (i, order) {
                                    $goTableBody.append(makeGoodsOrder(order));
                                });
                            } else {
                                if (init) {
                                    $goTableBody.append(emptyGoBlock(messageBundle['there.nothing']));
                                }
                            }
                        }
                    }).fail(function (jqXHR, textStatus, error) {
                        $goTableBody.append(emptyGoBlock(jqXHR.responseText));
                    }).always(function () {
                        loadingData = false;

                        $goLoading.detach();

                        if (currentPage < pagesCount) {
                            $loadMore.show();
                        }
                    });
                }

                function makeGoodsOrder(order) {
                    return $('<tr/>', {
                        append: $('<td/>', {
                            append: $('<a/>', {
                                href: '#',
                                text: order.orderNumber,
                                click: function () {
                                    connector.showGoodsOrderDetail(order.orderNumber);
                                }
                            })
                        }).add($('<td/>', {
                            text: order.dateTime
                        })).add($('<td/>', {
                            text: order.sum,
                            append: $('<span/>', {
                                text: 'HC'
                            }),
                            prepend: $('<i/>')
                        })).add($('<td/>', {
                            'status-code': order.statusCode,
                            text: order.status
                        }))
                    });
                }

                function emptyGoBlock(message) {
                    return $('<tr/>', {
                        append: $('<td/>', {
                            class: 'rcg-sh-go-empty',
                            colspan: 4,
                            text: message
                        })
                    })
                }
            }

            function openTab() {
                currentPage = recordsCount = pagesCount = 0;
                $contentBody.empty();

                if (tabId === 1) {
                    initCatalog();
                } else {
                    initOrders();
                }
            }
        } else {
            currentPage = recordsCount = pagesCount = 0;

            $contentBody.append($('<div/>', {
                class: 'rcg-gc-h',
                text: messageBundle['cart']
            }));

            var $gcList = $('<div/>', {
                    class: 'rcg-gc-list'
                }), $gcLoading = $('<div/>', {
                    class: 'rcg-gc-loading'
                }), $sumError = $('<div/>', {
                    class: 'rcg-gc-total-error',
                    text: messageBundle['hc.not.enough']
                }), $issueBtn = $('<a/>', {
                    class: 'rcg-gc-issue rcg-btn rcg-blue-btn rcg-btn-md',
                    text: messageBundle['checkout'],
                    click: function () {
                        if (totalSum <= balance) {
                            connector.openConfirmCheckout(totalSum);
                        }
                    }
                }),
                $totalSumWrapper = $('<span/>', {
                    class: 'rcg-gc-total-sum',
                    text: totalSum
                }),
                $totalWrapper = $('<div/>', {
                    class: 'rcg-gc-total-w',
                    append: $('<div/>', {
                        class: 'rcg-gc-total-h clearfix',
                        append: $('<div/>', {
                            class: 'rcg-gc-total-t',
                            text: messageBundle['total'] + (hasDiscount ? ' (-' + heartAwardDiscount + '%)' : '')
                        }).add($('<div/>', {
                            class: 'rcg-gc-total-v',
                            append: $('<span/>')
                                .add($totalSumWrapper)
                                .add($('<span/>', {
                                    class: 'rcg-gc-total-hc',
                                    text: ' HC'
                                }))
                        }))
                    }).add($sumError).add($issueBtn)
                });

            if (totalSum < balance) {
                $sumError.hide();
            } else {
                $issueBtn.addClass('rcg-gc-issue-d');
            }

            $('<div/>', {
                class: 'row',
                append: $('<div/>', {
                    class: 'col-lg-9',
                    append: $gcList
                }).add($('<div/>', {
                    class: 'col-lg-3 rcg-reset-col-l',
                    append: $totalWrapper
                }))
            }).appendTo($contentBody);

            function calculateTotalSum() {
                var $counters = $gcList.find('.rcg-gc-counter-w');
                var sum = 0;
                if ($counters) {
                    $counters.each(function (k, v) {
                        var _self = $(v),
                            quantity = _self.attr('quantity'),
                            price = _self.attr('price');
                        sum += quantity * price;
                    });

                    if (hasDiscount) {
                        sum = sum - Math.round(sum * Number(heartAwardDiscount) / 100);
                    }
                }
                $totalSumWrapper.text(sum);

                totalSum = sum;
                if (totalSum < balance) {
                    $sumError.hide();
                    $issueBtn.removeClass('rcg-gc-issue-d');
                } else {
                    $sumError.show();
                    $issueBtn.addClass('rcg-gc-issue-d');
                }
            }

            $win.unbind('calculateTotalSumEvent');
            $win.bind('calculateTotalSumEvent', function () {
                calculateTotalSum();
            });

            loadCart();

            loadingFunction = function () {
                loadCart();
            };

            function loadCart() {
                var init = currentPage === 0;
                currentPage++;

                $.ajax({
                    'url': webAppUrl + "/rest/v2/services/tsadv_RecognitionService/loadCart",
                    data: {
                        page: currentPage,
                        lastCount: recordsCount
                    },
                    'dataType': 'json',
                    'beforeSend': function (request) {
                        $gcList.append($gcLoading);

                        request.setRequestHeader("Authorization", "Bearer " + accessToken);
                        request.setRequestHeader("Accept-Language", language);

                        if (init) {
                            $gcList.empty();
                        }
                    }
                }).done(function (data, textStatus, jqXHR) {
                    if (data) {
                        pagesCount = Number(data.pageInfo.pagesCount);
                        recordsCount = Number(data.pageInfo.totalRowsCount);

                        var goods = data.goods;
                        if (recordsCount > 0 && (goods && goods.length > 0)) {
                            $.each(goods, function (i, item) {
                                $gcList.append(makeGc(item));
                            });
                        } else {
                            if (init) {
                                $gcList.append(emptyBlock(messageBundle['there.nothing']));
                            }
                        }

                        initTooltip();
                    }
                }).fail(function (jqXHR, textStatus, error) {
                    $gcList.append(emptyBlock(jqXHR.responseText));
                }).always(function () {
                    loadingData = false;

                    $gcLoading.detach();
                });

                function updateGoodsQuantity(cartId, goodsId, quantity, $counterWrapper, $count) {
                    if (!cartId || !goodsId) {
                        alert(messageBundle['alert.error.1']);
                        return;
                    }

                    if (!quantity || quantity < 1) {
                        alert(messageBundle['alert.error.2']);
                        return;
                    }

                    var data = {
                        goodsCartId: cartId,
                        goodsId: goodsId,
                        quantity: quantity
                    };

                    $.ajax({
                        url: webAppUrl + "/rest/v2/services/tsadv_RecognitionService/updateGoodsQuantity",
                        type: "POST",
                        contentType: "application/json",
                        data: JSON.stringify(data),
                        headers: {
                            "Authorization": "Bearer " + accessToken,
                            "Accept-Language": language
                        },
                        beforeSend: function (request) {
                            $counterWrapper.addClass('rcg-gc-counter-w-loading');
                        }
                    }).done(function (data, textStatus, jqXHR) {
                        if (data === 1) {
                            $counterWrapper.attr('quantity', quantity);
                            $count.text(quantity + ' ' + messageBundle['quantity.sh']);

                            calculateTotalSum();
                        }
                    }).fail(function (jqXHR, textStatus, error) {
                        alert('fail:' + jqXHR.responseText);
                    }).always(function (res) {
                        $counterWrapper.removeClass('rcg-gc-counter-w-loading');
                    });
                }

                function makeGc(gc) {
                    var quantity = gc.quantity;

                    var $count = $('<span/>', {
                        class: 'rcg-gc-counter-v',
                        text: quantity + ' ' + messageBundle['quantity.sh']
                    });

                    var $counterWrapper = $('<div/>', {
                        class: 'rcg-gc-counter-w',
                        quantity: quantity,
                        price: gc.price,
                        append: $('<i/>', {
                            class: 'fa fa-minus',
                            click: function () {
                                var q = Number($counterWrapper.attr('quantity'));
                                if (q === 1) {
                                    return false;
                                }

                                updateGoodsQuantity(gc.cartId, gc.id, --q, $counterWrapper, $count);
                            }
                        }).add($count).add($('<i/>', {
                            class: 'fa fa-plus',
                            click: function () {
                                var q = Number($counterWrapper.attr('quantity'));
                                updateGoodsQuantity(gc.cartId, gc.id, ++q, $counterWrapper, $count);
                            }
                        })).add($('<span/>', {
                            class: 'rcg-gc-counter-w-loader'
                        }))
                    });

                    return $('<div/>', {
                        class: 'row rcg-sh-g2-w rcg-sh-gc-w',
                        'goods-cart': gc.cartId,
                        append: $('<div/>', {
                            class: 'col-lg-3 rcg-sh-g2-image-w',
                            append: $('<img/>', {
                                class: 'rcg-sh-g2-image',
                                src: gc.image
                            })
                        }).add($('<div/>', {
                            class: 'col-lg-6 rcg-sh-g2-body',
                            append: $('<div/>', {
                                class: 'rcg-sh-g2-name',
                                text: gc.name
                            }).add($counterWrapper)
                        })).add($('<div/>', {
                            class: 'col-lg-3 rcg-sh-g2-pw',
                            append: $('<div/>', {
                                class: 'rcg-sh-g2-price',
                                text: gc.price,
                                append: $('<span/>', {
                                    text: ' HC'
                                }),
                                prepend: $('<span/>')
                            }).add($('<a/>', {
                                class: 'rcg-gc-remove',
                                text: messageBundle['remove'],
                                click: function (e) {
                                    e.stopImmediatePropagation();
                                    connector.removeGoodsCart(gc.cartId);
                                }
                            }))
                        }))
                    });
                }

                function emptyBlock(message) {
                    return $('<div/>', {
                        class: 'col-lg-12',
                        append: $('<div/>', {
                            class: 'rcg-team-empty',
                            text: message
                        })
                    })
                }
            }
        }

        function initTemplate() {
            var $leftMenuUl = $('<ul/>', {
                    class: 'rcg-sh-h-menu rcg-sh-h-menu-l',
                    'active-tab': tabId
                }),
                isShop = pageName === 'shop';

            if (isShop) {
                $leftMenuUl.append($('<li/>', {
                    text: messageBundle['catalog'],
                    click: function () {
                        activateTab(this, 1);
                    }
                })).append($('<li/>', {
                    text: messageBundle['my.orders'] + ' ' + goodsOrdersCount,
                    click: function () {
                        activateTab(this, 2);
                    }
                }));
            } else {
                $leftMenuUl.append($('<li/>', {
                    text: messageBundle['return.to.shop'],
                    prepend: $('<i/>', {
                        class: 'rcg-sh-h-menu-icon fa fa-chevron-left'
                    }),
                    click: function () {
                        connector.openShop();
                    }
                }));
            }

            var $headerRow = $('<div/>', {
                class: 'row rcg-sh-h-row rcg-sh-h-fixed',
                append: $('<div/>', {
                    class: 'col-lg-6',
                    append: $leftMenuUl
                }).add($('<div/>', {
                    class: 'col-lg-6',
                    append: $('<ul/>', {
                        class: 'rcg-sh-h-menu rcg-sh-h-menu-r',
                        append: $('<li/>', {
                            text: messageBundle['your.balance'],
                            append: $('<span/>', {
                                class: 'rcg-sh-h-menu-balance',
                                text: balance,
                                prepend: $('<i/>', {
                                    class: 'rcg-sh-h-menu-coin-icon'
                                }),
                                append: $('<span/>', {
                                    text: 'HC'
                                })
                            })
                        }).add($('<li/>', {
                            append: $rcgCart
                        }))
                    })
                }))
            });

            function activateTab(li, t) {
                $(li).parent().attr('active-tab', t);
                tabId = t;
                openTab()
            }

            $(element).append($headerRow).append($contentBody);
        }
    }

    var tps = $('.tippy-popper');
    if (tps) tps.remove();
};