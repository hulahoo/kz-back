kz_uco_tsadv_web_toolkit_ui_rcgwallcomponent_RcgWallComponent = function () {
    var connector = this, element = connector.getElement(), TAB_INDEX = 1,
        state = connector.getState(),
        currentPage = 0,
        recordsCount = 0,
        pagesCount = 0,
        accessToken = state.authorizationToken,
        wallType = state.wallType,
        organizationGroupId = "-1",
        pageName = state.pageName,
        currentProfilePojo = state.currentProfilePojo,
        profilePojo = state.profilePojo,
        webAppUrl = state.webAppUrl,
        questionPojo = state.questionPojo,
        reloadPreferences = null,
        sideMenuResizeHandler = null,
        currentTab = 'RCG',
        loadingFunction = null,
        refreshRecognitionWall = null,
        language = state.language,
        accessNominee = state.accessNominee,
        messageBundle = JSON.parse(state.messageBundle),
        chartFeedbackTypes = state.chartFeedbackTypes ? JSON.parse(state.chartFeedbackTypes) : {};

    var commentModel = {
        commentPojo: {
            recognitionId: null,
            text: null,
            parentCommentId: null
        }
    };

    $(window).on('sidemenu-resize', function () {
        if (sideMenuResizeHandler) {
            sideMenuResizeHandler.apply();
        }
    });

    connector.onStateChange = function () {
        var state = connector.getState(), ra = state.reloadAttributes;
        if (ra) {
            if (ra === 'preferences') {
                if (reloadPreferences) {
                    profilePojo = state.profilePojo;
                    reloadPreferences.apply();
                }
            } else if (ra === 'recognitions') {
                if (refreshRecognitionWall !== null) {
                    refreshRecognitionWall.apply();
                }
            }
        }
    };

    connector.reloadFeedback = function (feedback) {
        var event = $.Event("reloadFeedbackEvent");
        event.feedback = feedback;
        $win.trigger(event);
    };

    connector.refreshFeedback = function () {
        $win.trigger($.Event("refreshFeedbackEvent"));
    };

    if (pageName === 'profile' || pageName === 'main' || pageName === 'feedback') {
        var loadingOnScroll = false;

        if (pageName === 'main') {
            $('<div/>', {
                class: 'row rcg-mmc-header',
                append: $('<div/>', {
                    class: 'col-lg-6 col-md-6 col-sm-6 col-xs-12 rcg-mmc-header-l rcg-align-l',
                    append: $('<div/>', {
                        class: 'rcg-hello-text',
                        text: messageBundle['welcome.text'] + ' ' + currentProfilePojo.firstName + '!'
                    }).add($('<div/>', {
                        class: 'rcg-hello-text-2',
                        text: messageBundle['welcome.text.2']
                    }))
                }).add($('<div/>', {
                    class: 'col-lg-6 col-md-6 col-sm-6 col-xs-12 rcg-mmc-header-r rcg-align-r',
                    append: $('<a/>', {
                        href: '#',
                        class: 'rcg-thanks-link rcg-btn rcg-blue-btn rcg-btn-md',
                        text: messageBundle['send.rcg'],
                        prepend: $('<i/>', {
                            class: 'fa fa-thumbs-up'
                        }),
                        click: function () {
                            connector.giveThanks(profilePojo.pgId);
                        }
                    }).add($('<a/>', {
                        href: '#',
                        class: 'rcg-nominate-link rcg-btn rcg-white-btn rcg-btn-md',
                        text: messageBundle['nominate'],
                        prepend: $('<i/>'),
                        click: function () {
                            connector.nominate(profilePojo.pgId);
                        }
                    }))
                }))
            }).appendTo($(element));
        } else if (pageName === 'feedback') {
            $('<div/>', {
                class: 'row rcg-mmc-header',
                append: $('<div/>', {
                    class: 'col-lg-6 col-md-6 col-sm-6 col-xs-12 rcg-mmc-header-l rcg-align-l',
                    append: $('<div/>', {
                        class: 'rcg-hello-text',
                        text: messageBundle['welcome.text'] + ' ' + currentProfilePojo.firstName + '!'
                    }).add($('<div/>', {
                        class: 'rcg-hello-text-2',
                        text: messageBundle['welcome.text.2']
                    }))
                }).add($('<div/>', {
                    class: 'col-lg-6 col-md-6 col-sm-6 col-xs-12 rcg-mmc-header-r rcg-align-r',
                    append: $('<a/>', {
                        href: '#',
                        class: 'rcg-feedback-send-btn rcg-btn rcg-blue-btn rcg-btn-md',
                        text: messageBundle['rcg.feedback.send.btn'],
                        prepend: $('<i/>', {
                            class: 'fa fa-comments'
                        }),
                        click: function () {
                            connector.openFeedbackEditor('SEND');
                        }
                    }).add($('<a/>', {
                        href: '#',
                        class: 'rcg-feedback-receive-btn rcg-btn rcg-white-btn rcg-btn-md',
                        text: messageBundle['rcg.feedback.receive.btn'],
                        prepend: $('<i/>'),
                        click: function () {
                            connector.openFeedbackEditor('REQUEST');
                        }
                    }))
                }))
            }).appendTo($(element));
        }

        var $row = $('<div/>', {
                class: 'row'
            }),
            $footer = $('<div/>', {
                class: 'rcg-page-footer'
            });

        $(element).append($row).append($footer);

        //init blocks
        var $leftBlock = null,
            $rightBlock = null,
            $links = null;

        var $rcgWallWrapper = $('<div/>', {
            class: 'rcg-wall-wrapper'
        });

        var $rcgWallWrapperHeader = $('<div/>', {
            class: 'rcg-pr-content-h'
        }).appendTo($rcgWallWrapper);

        var $rcgWall = $("<div/>", {
            class: 'rcg-wall rcg-wall-init'
        }).appendTo($rcgWallWrapper);

        if (pageName === 'main') {
            //#24a16f
            var $mainInformation = $('<div/>', {
                class: 'rcg-m-info bg-green',
                text: messageBundle['show.rcg.more'],
                append: $('<br/>')
                    .add($('<a/>', {
                        href: '#',
                        class: 'rcg-m-info-link',
                        text: messageBundle['read.more'],
                        click: function () {
                            connector.openFaq();
                        }
                    }).add($('<i/>', {
                        class: 'fa fa-times rcg-tooltip',
                        'data-tippy-theme': 'dark',
                        title: messageBundle['hide'],
                        click: function () {
                            $mainInformation.remove();
                        }
                    })))
            });

            $leftBlock = $('<div/>', {
                class: 'col-lg-8 col-md-8 col-sm-8 col-xs-12 rcg-reset-col',
                append: $mainInformation
                    .add($rcgWallWrapper)
            });

            $rightBlock = $('<div/>', {
                class: 'col-lg-4 col-md-4 col-sm-4  col-xs-12 rcg-xs-mt rcg-reset-col-r'
            });

            // Top ten
            var $topTenTable = $('<table/>', {
                class: 'rcg-tt-table'
            });

            var $topTenTabs = $('<ul/>', {
                class: 'rcg-block-b-tabs',
                append: $('<li/>', {
                    append: $('<a/>', {
                        text: messageBundle['top.awarded.tab'],
                        click: function () {
                            loadTopTenContent(0)
                        }
                    })
                }).add($('<li/>', {
                    append: $('<a/>', {
                        text: messageBundle['top.sender.tab'],
                        click: function () {
                            loadTopTenContent(1)
                        }
                    })
                }))
            });

            var $topTenLoader = $('<span/>', {
                class: 'rcg-tt-loader'
            });

            var showAllText = messageBundle['show.all'],
                slideUpText = messageBundle['slide.up'],
                $toggleLink = $('<a/>', {
                    class: 'rcg-tt-show-all',
                    'rcg-tt-vm': 'min',
                    text: showAllText,
                    click: function () {
                        var _self = $(this);
                        var vm = _self.attr('rcg-tt-vm');
                        if (vm === 'min') {
                            $topTenTable.find('tr:gt(3)').show();
                            _self.attr('rcg-tt-vm', 'max');
                            _self.text(slideUpText);
                        } else {
                            $topTenTable.find('tr:gt(3)').hide();
                            _self.attr('rcg-tt-vm', 'min');
                            _self.text(showAllText);
                        }
                    }
                });

            $('<div/>', {
                class: 'rcg-block-w',
                append: $('<div/>', {
                    class: 'rcg-block-h',
                    text: messageBundle['top.ten'],
                    append: $topTenLoader
                }).add($('<div/>', {
                    class: 'rcg-block-b',
                    append: $topTenTabs
                        .add($('<div/>', {
                            class: 'rcg-block-b-content',
                            append: $topTenTable
                        }))
                })).add($('<div/>', {
                    class: 'rcg-block-f',
                    append: $toggleLink
                }))
            }).appendTo($rightBlock);

            loadTopTenContent(0);

            loadBanners('main', $rightBlock);

            //loadComingBirthdays
            /*var $cbLoader = $topTenLoader.clone(),
                $cbTable = $topTenTable.clone(),
                $cbToggle = $('<a/>', {
                    class: 'rcg-tt-show-all',
                    'rcg-tt-vm': 'min',
                    text: 'Показать всех',
                    click: function () {
                        var _self = $(this);
                        var vm = _self.attr('rcg-tt-vm');
                        if (vm === 'min') {
                            $cbTable.find('tr:gt(3)').show();
                            _self.attr('rcg-tt-vm', 'max');
                            _self.text('Свернуть');
                        } else {
                            $cbTable.find('tr:gt(3)').hide();
                            _self.attr('rcg-tt-vm', 'min');
                            _self.text('Показать всех');
                        }
                    }
                });

            $('<div/>', {
                class: 'rcg-block-w',
                append: $('<div/>', {
                    class: 'rcg-block-h',
                    text: 'Ближайшие дни рождения',
                    append: $cbLoader
                }).add($('<div/>', {
                    class: 'rcg-block-b',
                    append: $cbTable
                })).add($('<div/>', {
                    class: 'rcg-block-f',
                    append: $cbToggle
                }))
            }).appendTo($rightBlock);

            loadComingBirthday();

            function loadComingBirthday() {
                $cbTable.empty();

                $.ajax({
                    'url': webAppUrl + "/rest/v2/services/tsadv_RecognitionService/loadComingBirthdays",
                    'dataType': 'json',
                    'beforeSend': function (request) {
                        request.setRequestHeader("Authorization", "Bearer " + accessToken);
                    }
                }).done(function (data, textStatus, jqXHR) {
                    if (data) {
                        if (data.length > 0) {
                            $.each(data, function (i, item) {
                                $cbTable.append(makeComingBirthday(item));
                            });
                        } else {
                            $('<label/>', {
                                class: 'rcg-list-empty-label',
                                text: messageBundle['there.nothing']
                            }).appendTo($cbTable);
                        }
                    }
                }).fail(function (jqXHR, textStatus, error) {
                    $cbTable.addClass('rcg-list-empty');

                    $('<label/>', {
                        class: 'rcg-list-empty-label',
                        text: jqXHR.responseText
                    }).appendTo($cbTable);
                }).always(function () {
                    $cbLoader.hide();

                    $cbTable.find('tr:gt(3)').hide();
                });

                function makeComingBirthday(item) {
                    var ha = item.heartAward ? $('<i/>', {
                        class: 'ha'
                    }) : null;

                    var $tr = $('<tr/>', {
                        append: $('<td/>', {
                            append: $('<img/>', {
                                class: 'rcg-tt-image',
                                src: item.image
                            })
                        }).add($('<td/>', {
                            append: $('<div/>', {
                                class: 'rcg-tt-fn',
                                append: $('<a/>', {
                                    text: item.fullName,
                                    click: function () {
                                        connector.openProfilePage(item.pgId);
                                    }
                                }).add(ha)
                            }).add($('<div/>', {
                                class: 'rcg-tt-org',
                                text: item.birthday
                            }))
                        }))
                    });
                    return $tr;
                }
            }*/

            function loadTopTenContent(tab) {
                $topTenLoader.show();

                //reset link
                $toggleLink.attr('rcg-tt-vm', 'min').text(messageBundle['show.all']);

                var li = $topTenTabs.find('li');
                $.each(li, function (k, v) {
                    $(v).removeClass('b-tab-active');
                });
                li.eq(tab).addClass('b-tab-active');

                var method = tab === 0 ? 'loadTopAwarded' : 'loadTopSender';

                loadTopTenData(method);

                function loadTopTenData(method) {
                    $topTenTable.empty();

                    $.ajax({
                        'url': webAppUrl + "/rest/v2/services/tsadv_RecognitionService/" + method,
                        'dataType': 'json',
                        'beforeSend': function (request) {
                            request.setRequestHeader("Authorization", "Bearer " + accessToken);
                            request.setRequestHeader("Accept-Language", language);
                        }
                    }).done(function (data, textStatus, jqXHR) {
                        if (data) {
                            if (data.length > 0) {
                                $.each(data, function (i, item) {
                                    $topTenTable.append(makeTopTen(item));
                                });
                            } else {
                                $('<label/>', {
                                    class: 'rcg-list-empty-label',
                                    text: messageBundle['there.nothing']
                                }).appendTo($topTenTable);
                            }
                        }
                    }).fail(function (jqXHR, textStatus, error) {
                        $topTenTable.addClass('rcg-list-empty');

                        $('<label/>', {
                            class: 'rcg-list-empty-label',
                            text: jqXHR.responseText
                        }).appendTo($topTenTable);
                    }).always(function () {
                        $topTenLoader.hide();

                        $topTenTable.find('tr:gt(3)').hide();
                    });
                }

                function makeTopTen(item) {
                    var ha = item.heartAward ? $('<span/>', {
                        class: 'rcg-tt-ha',
                        append: $('<i/>', {
                            class: 'rcg-tooltip',
                            title: item.heartAward
                        })
                    }) : null;

                    var $tr = $('<tr/>', {
                        append: $('<td/>', {
                            append: $('<img/>', {
                                class: 'rcg-tt-image',
                                src: item.image
                            })
                        }).add($('<td/>', {
                            append: $('<div/>', {
                                class: 'rcg-tt-fn',
                                append: $('<a/>', {
                                    text: item.fullName,
                                    click: function () {
                                        connector.openProfilePage(item.pgId);
                                    }
                                }).add(ha)
                            }).add($('<div/>', {
                                class: 'rcg-tt-org',
                                text: item.position
                            }))
                        })).add($('<td/>', {
                            class: 'rcg-tt-count',
                            text: item.count,
                            prepend: $('<i/>', {
                                class: 'rcg-tt-medal-' + item.medal
                            })
                        }))
                    });
                    return $tr;
                }
            }

            $links = $('<ul>', {
                class: 'rcg-pr-content-h-tabs',
                append: createTab(messageBundle['company.activity'], null, true)
            });
        } else if (pageName === 'feedback') {
            var $feedbackInformation = $('<div/>', {
                class: 'rcg-m-info bg-orange',
                text: messageBundle['show.rcg.feedback.info'],
                append: $('<br/>')
            });

            $leftBlock = $('<div/>', {
                class: 'col-lg-8 col-md-8 col-sm-8 col-xs-12 rcg-reset-col',
                append: $feedbackInformation
                    .add($rcgWallWrapper)
            });

            var $title = $('<div/>', {
                class: 'rcg-block-h rf-chart-title',
                text: messageBundle['rcg.feedback.chart.title']
            });

            $rightBlock = $('<div/>', {
                class: 'col-lg-4 col-md-4 col-sm-4  col-xs-12 rcg-xs-mt rcg-reset-col-r',
                append: $('<div>', {
                    class: 'rcg-block-w rf-chart-w',
                    append: $title.add($('<div/>', {
                        id: 'rf-chart',
                        class: 'rcg-block-b rf-chart'
                    }))
                })
            });

            $links = $('<ul>', {
                class: 'rcg-pr-content-h-tabs',
                append: createTab(messageBundle['rcg.feedback.receive'], function () {
                    currentTab = 'RECEIVE';
                    $title.text(messageBundle['rcg.feedback.chart.title']);
                    resetLink(this);
                    loadRcgFeedbackTab();
                }, true).add(createTab(messageBundle['rcg.feedback.send'], function () {
                    currentTab = 'SEND';
                    $title.text(messageBundle['rcg.feedback.chart.title.1']);
                    resetLink(this);
                    loadRcgFeedbackTab();
                })).add(createTabWithCount(messageBundle['rcg.feedback.required'], function () {
                        currentTab = 'REQUEST';
                        resetLink(this);
                        loadRcgFeedbackTab();
                        refreshTabCounter(2);
                    }, false, 2) // index started 0
                )
            });

            loadBanners('feedback', $rightBlock);
        } else if (pageName === 'profile') {
            $leftBlock = $('<div/>', {
                class: 'col-lg-4 col-md-4 col-sm-4 col-xs-12 rcg-reset-col-l'
            });

            $rightBlock = $('<div/>', {
                class: 'col-lg-8 col-md-8 col-sm-8 col-xs-12 rcg-xs-mt rcg-reset-col'
            });

            var heartAward = profilePojo.heartAward,
                $file = $('<input/>', {
                    type: 'file',
                    class: 'rcg-dn',
                    change: function (e) {
                        var files = e.target.files;
                        if (files) {
                            uploadProfileImage(files[0]);
                        }
                    }
                }),
                $profileImage = $('<img/>', {
                    class: 'rcg-pr-image',
                    src: profilePojo.image,
                    click: function () {
                        if (wallType === 0) {
                            $file.trigger('click');
                        }
                    }
                });

            var $profileImageWrap = $('<div/>', {
                class: 'rcg-pr-image-wrap',
                append: $profileImage
            });

            if (wallType === 0) { //self
                $profileImageWrap.append($file);

                if (profilePojo.showImageTooltip) {
                    $profileImage.addClass('rcg-pr-image-null');
                    $profileImage.attr('title', messageBundle['hc.for.image']);
                    $profileImage.attr('data-tippy-theme', 'blue');
                    $profileImage.attr('data-tippy-placement', 'top');
                }
            }

            function uploadProfileImage(file) {
                var reader = new FileReader();
                reader.readAsBinaryString(file);

                reader.onload = function (e) {
                    var sendData = {};
                    sendData.imageContent = btoa(e.target.result);
                    $.ajax({
                        url: webAppUrl + "/rest/v2/services/tsadv_RecognitionService/uploadProfileImage",
                        type: "POST",
                        contentType: "application/json",
                        data: JSON.stringify(sendData),
                        headers: {
                            "Authorization": "Bearer " + accessToken,
                            "Accept-Language": language
                        },
                        beforeSend: function (request) {
                            //show loader
                        }
                    }).done(function (data, textStatus, jqXHR) {
                        destroyTooltip('.rcg-pr-image-null');

                        $profileImage.removeClass('rcg-pr-image-null');
                        $profileImage.removeAttr('title data-tippy-theme data-tippy-placement');

                        var $coinsCount = $('.rcg-pr-coins-count');
                        var count = Number($coinsCount.text());
                        count += Number(data);
                        $coinsCount.text(count);

                        connector.reloadProfileImage();
                    }).fail(function (jqXHR, textStatus, error) {
                        alert('fail:' + jqXHR.responseText);
                    }).always(function (res) {
                        //hide loader
                    })
                };
            }

            if (heartAward) {
                $profileImageWrap.addClass('rcg-heart-award rcg-tooltip');
                $profileImageWrap.attr('title', heartAward);
                $profileImageWrap.append($('<i/>', {
                    class: 'rcg-heart-award-icon'
                }));
            }

            var $profileWrapper = $('<div/>', {
                class: 'rcg-pr-wrapper rcg-block',
                append: $('<div/>', {
                    class: 'row rcg-pr-info',
                    append: $('<div/>', {
                        class: 'rcg-pr-info-image col-lg-12 col-md-12 col-sm-4 col-xs-2 rcg-reset-col',
                        append: $profileImageWrap
                    }).add($('<div/>', {
                        class: 'rcg-pr-info-np col-lg-12 col-md-12 col-sm-8 col-xs-9 rcg-reset-col',
                        append: $('<div/>', {
                            class: 'rcg-pr-name',
                            text: profilePojo.fullName
                        }).add($('<div/>', {
                            class: 'rcg-pr-position',
                            text: profilePojo.position
                        })).add($('<div/>', {
                            class: 'rcg-pr-organization',
                            text: profilePojo.organization
                        }))
                    }))
                })
            });

            if (wallType === 0) { //self
                if (questionPojo) {
                    var $answers = $('<div/>', {
                        class: 'rcg-question-answer-w'
                    });

                    var answers = questionPojo.answers;

                    if (answers && answers.length > 0) {
                        $answers.attr('data-type', questionPojo.type);

                        var isIcon = questionPojo.type === 'ICON',
                            checkedAnswer = '-1';
                        $.each(answers, function (k, item) {
                            if (isIcon) {
                                $('<img/>', {
                                    src: item.image,
                                    click: function () {
                                        checkedAnswer = item.stringId;
                                        $answers.find('img').removeAttr('selected');
                                        $(this).attr('selected', true);
                                    }
                                }).appendTo($answers);
                            } else {
                                var key = 'rcg-qa-' + k;
                                $('<div/>', {
                                    append: $('<input/>', {
                                        type: 'radio',
                                        id: key,
                                        name: 'answer',
                                        click: function () {
                                            checkedAnswer = item.stringId;
                                        }
                                    }).add($('<label/>', {
                                        for: key,
                                        text: item.text
                                    }))
                                }).appendTo($answers);
                            }
                        });
                    }

                    var $questionWrapper = $('<div/>', {
                        class: 'rcg-question-w'
                    });

                    $('<div/>', {
                        class: 'rcg-question-h',
                        append: $('<i/>', {
                            class: 'rcg-question-info fa fa-info-circle rcg-tooltip',
                            'data-tippy-flip': 'false',
                            title: questionPojo.description
                        }).add($('<div/>', {
                            class: 'rcg-question-text',
                            text: questionPojo.text
                        })).add($('<i/>', {
                            class: 'rcg-question-close fa fa-times rcg-tooltip',
                            'data-tippy-flip': 'false',
                            'data-tippy-theme': 'dark',
                            title: messageBundle['hide'],
                            click: function () {
                                $questionWrapper.slideUp("normal", function () {
                                    $(this).remove();
                                });
                            }
                        }))
                    }).appendTo($questionWrapper);
                    $questionWrapper.append($answers);
                    $questionWrapper.append($('<div/>', {
                        class: 'rcg-question-f',
                        append: $('<a/>', {
                            class: 'rcg-question-answer-link rcg-btn rcg-blue-btn rcg-btn-sm',
                            text: messageBundle['answer'],
                            click: function () {
                                if (checkedAnswer !== '-1') {
                                    connector.sendQuestionAnswer(checkedAnswer);
                                }
                            },
                            prepend: $('<i/>', {
                                class: 'fa fa-paper-plane'
                            })
                        })
                    }));

                    $leftBlock.append($questionWrapper);
                }

                $('<div/>', {
                    class: 'rcg-pr-coins row',
                    append: $('<div/>', {
                        class: 'col-lg-6 col-md-6 col-sm-6 col-xs-6 rcg-pr-coins-l',
                        append: $('<div/>', {
                            class: 'rcg-pr-coins-block',
                            append: $('<i/>', {
                                class: 'rcg-points-icon'
                            })
                        }).add($('<div/>', {
                            class: 'rcg-pr-coins-text',
                            text: 'Points',
                            prepend: $('<span/>', {
                                text: currentProfilePojo.points
                            })
                        }))
                    }).add($('<div/>', {
                        class: 'col-lg-6 col-md-6 col-sm-6 col-xs-6 rcg-pr-coins-r',
                        append: $('<div/>', {
                            class: 'rcg-pr-coins-block',
                            append: $('<i/>', {
                                class: 'rcg-coins-icon'
                            })
                        }).add($('<div/>', {
                            class: 'rcg-pr-coins-text',
                            text: 'HEART Coins',
                            prepend: $('<span/>', {
                                class: 'rcg-pr-coins-count',
                                text: currentProfilePojo.coins
                            })
                        }))
                    }))
                }).appendTo($profileWrapper);

                /*.add(createTab(messageBundle['my.statistic'], function () {
                    currentTab = 'STATISTIC';
                    resetLink(this);
                }))*/
                $links = $('<ul>', {
                    class: 'rcg-pr-content-h-tabs',
                    append: createTab(messageBundle['my.recognitions'], function () {
                        currentTab = 'RCG';
                        resetLink(this);
                        loadThanksTab();
                    }, true).add(createTab(messageBundle['my.coins'], function () {
                        currentTab = 'LOG';
                        resetLink(this);
                        loadCoinLogTab();
                    }))
                });

                if (currentProfilePojo.medalCount && currentProfilePojo.medalCount > 0) {
                    //medals block
                    var $medalsBody = $('<div/>', {
                        class: 'rcg-pr-medals-body rcg-block-b clearfix'
                    });

                    var $medalsWrapper = $('<div/>', {
                        class: 'rcg-pr-medals-wrapper rcg-block-w',
                        append: $('<div/>', {
                            class: 'rcg-pr-medals-title rcg-block-h',
                            text: messageBundle['awards'],
                            append: $('<i/>', {
                                class: 'fa fa-info-circle rcg-tooltip',
                                title: messageBundle['employee.awards']
                            })
                        }).add($medalsBody)
                    }).appendTo($rightBlock);

                    $(window).on('resize', function () {
                        calculateMedalBody(undefined);
                    });

                    loadPersonMedals();

                    function calculateMedalBody($moreLink) {
                        if ($moreLink === undefined) {
                            $moreLink = $medalsBody.find('.rcg-pr-medals-more');
                        }

                        var allIn = $moreLink.attr('all-in');
                        if (allIn === '0') {
                            var $medals = $medalsBody.find('.rcg-medal-w');

                            var bodyWidth = $medalsBody.width(),
                                availableSize = bodyWidth / 75,
                                totalSize = $medals.length;

                            $.each($medals, function (k, item) {
                                if (k < availableSize - 1) {
                                    $(item).show();
                                } else {
                                    $(item).hide();
                                }
                            });

                            if (totalSize > availableSize) {
                                $moreLink.show();
                            } else {
                                $moreLink.hide();
                            }
                        }
                    }

                    function loadPersonMedals() {
                        $.ajax({
                            'url': webAppUrl + "/rest/v2/services/tsadv_RecognitionService/loadPersonMedals",
                            data: {
                                personGroupId: currentProfilePojo.pgId
                            },
                            'dataType': 'json',
                            'beforeSend': function (request) {
                                $medalsBody.empty();
                                $medalsWrapper.addClass('rcg-pr-medals-loading');

                                request.setRequestHeader("Authorization", "Bearer " + accessToken);
                                request.setRequestHeader("Accept-Language", language);
                            }
                        }).done(function (data, textStatus, jqXHR) {
                            if (data && data.length > 0) {
                                if ($medalsWrapper.hasClass('rcg-pr-medals-empty')) {
                                    $medalsWrapper.removeClass('rcg-pr-medals-empty');
                                }

                                $.each(data, function (i, item) {
                                    $medalsBody.append(makeMedal(item));
                                });

                                var $moreLink = $('<span/>', {
                                    class: 'rcg-pr-medals-more',
                                    text: messageBundle['more'],
                                    'all-in': '0',
                                    append: $('<i/>', {
                                        class: 'fa fa-chevron-down'
                                    }),
                                    click: function () {
                                        var _self = $(this), icon = _self.find('i');
                                        var allIn = _self.attr('all-in');
                                        if (allIn === undefined || !allIn) {
                                            allIn = '0';
                                        }

                                        _self.attr('all-in', Math.abs(Number(allIn) - 1));

                                        if (allIn === '0') {
                                            $medalsBody.find('.rcg-medal-w').show();
                                            icon.removeClass('fa-chevron-down').addClass('fa-chevron-up');
                                            setTextContents($moreLink, messageBundle['hide']);
                                        } else {
                                            icon.removeClass('fa-chevron-up').addClass('fa-chevron-down');
                                            setTextContents($moreLink, messageBundle['more']);
                                            calculateMedalBody($moreLink);
                                        }
                                    }
                                });

                                $moreLink.hide();
                                $medalsBody.append($moreLink);

                                calculateMedalBody($moreLink);
                            }
                        }).fail(function (jqXHR, textStatus, error) {
                            $medalsWrapper.addClass('rcg-pr-medals-empty');

                            $('<label/>', {
                                class: 'rcg-list-empty-label',
                                text: jqXHR.responseText
                            }).appendTo($medalsWrapper);
                        }).always(function () {
                            $medalsWrapper.removeClass('rcg-pr-medals-loading');
                        });

                        function setTextContents($elem, text) {
                            $elem.contents().filter(function () {
                                if (this.nodeType === Node.TEXT_NODE) {
                                    this.nodeValue = text;
                                }
                            });
                        }

                        function makeMedal(item) {
                            var count = item.count;
                            return $('<div/>', {
                                class: 'rcg-medal-w',
                                append: $('<img/>', {
                                    src: item.medalImage,
                                    class: 'rcg-medal-image rcg-tooltip',
                                    title: item.medalName
                                }).add($('<span/>', {
                                    class: 'rcg-medal-count ' + ((count && count === 1) ? 'fa fa-check' : ''),
                                    text: (count && count > 1) ? count : ''
                                }))
                            });
                        }
                    }

                }
            } else if (wallType === 1) { //other
                var $profileActions = $('<div/>', {
                    class: 'rcg-pr-actions',
                    append: $('<a/>', {
                        href: '#',
                        class: 'rcg-btn rcg-blue-btn rcg-btn-md',
                        text: messageBundle['send.rcg'],
                        prepend: $('<i/>', {
                            class: 'fa fa-thumbs-up'
                        }),
                        click: function () {
                            connector.giveThanks(profilePojo.pgId);
                        }
                    })
                });

                if (accessNominee === 1) {
                    $profileActions.append($('<a/>', {
                        href: '#',
                        class: 'rcg-pr-nominate-link rcg-btn rcg-white-btn rcg-btn-md',
                        text: messageBundle['nominate'],
                        prepend: $('<i/>'),
                        click: function () {
                            connector.nominate(profilePojo.pgId);
                        }
                    }));
                }

                $profileActions.append($('<a/>', {
                    href: '#',
                    class: 'rcg-pr-feedback-link rcg-btn rcg-white-btn rcg-btn-md',
                    text: messageBundle['rcg.feedback.send.btn'],
                    prepend: $('<i class="fa fa-comments"/>'),
                    click: function () {
                        connector.sendPersonFeedback(profilePojo.pgId);
                    }
                }));

                $profileWrapper.append($profileActions);

                $links = $('<ul>', {
                    class: 'rcg-pr-content-h-tabs',
                    append: createTab(messageBundle['recognitions'] + ' ' + profilePojo.firstName, function () {
                        resetLink(this);
                        loadThanksTab();
                    }, true)
                });

                /*var $sendCoin = $('<a/>', {
                    href: '#',
                    text: messageBundle['send'],
                    class: 'rcg-pr-shc-btn rcg-btn rcg-white-btn rcg-btn-sm rcg-disabled-btn',
                    click: function () {
                        var coins = $coinsInput.val();
                        if (!checkValue(coins)) {
                            return false;
                        }

                        connector.sendHeartCoins(profilePojo.pgId, coins, $('textarea.rcg-pr-shc-comment').val());
                    }
                });

                var $errorLabel = $('<span/>', {
                    class: 'rcg-pr-shc-error',
                    text: messageBundle['not.enough.points']
                });

                var coinsInputValidate = function () {
                    checkValue($(this).val());
                };

                function checkValue(val) {
                    if (val !== '' && val !== '0') {
                        if (Number(val) > Number(currentProfilePojo.points)) {
                            $errorLabel.show();

                            if (!$sendCoin.hasClass('rcg-disabled-btn')) {
                                $sendCoin.addClass('rcg-disabled-btn');
                            }
                        } else {
                            $errorLabel.hide();
                            $sendCoin.removeClass('rcg-disabled-btn');
                            return true;
                        }
                    } else {
                        $errorLabel.hide();

                        if (!$sendCoin.hasClass('rcg-disabled-btn')) {
                            $sendCoin.addClass('rcg-disabled-btn');
                        }
                    }
                    return false;
                }

                var $coinsInput = $('<input/>', {
                    type: 'number',
                    min: 0,
                    max: 1000,
                    value: '0',
                    maxlength: 5,
                    class: 'rcg-pr-shc-input rcg-number',
                    keyup: coinsInputValidate,
                    change: coinsInputValidate
                });

                //send heart coins block
                $('<div/>', {
                    class: 'rcg-pr-shc-wrapper',
                    append: $('<div/>', {
                        class: 'rcg-pr-shc-title',
                        text: messageBundle['send.hc'],
                        append: $('<i/>', {
                            class: 'fa fa-info-circle rcg-tooltip',
                            title: messageBundle['send.hc'],
                            'data-tippy-flip': 'false'
                        })
                    }).add($('<div/>', {
                        class: 'rcg-pr-shc-body',
                        append: $('<textarea/>', {
                            class: 'rcg-pr-shc-comment',
                            placeholder: messageBundle['your.comment']
                        }).add($('<div/>', {
                            class: 'rcg-pr-shc-f',
                            append: $('<div/>', {
                                class: 'rcg-pr-shc-input-wrap',
                                append: $('<i/>')
                                    .add($coinsInput)
                                    .add($errorLabel)
                            }).add($sendCoin)
                        }))
                    }))
                }).appendTo($rightBlock);*/
                if(pageName === 'profile' && wallType == 0) {
                    if (currentProfilePojo.medalCount && currentProfilePojo.medalCount > 0) {
                        //medals block
                        var $medalsBodyTeam = $('<div/>', {
                            class: 'rcg-pr-medals-body rcg-block-b clearfix'
                        });
                        var $medalsWrapperTeam = $('<div/>', {
                            class: 'rcg-pr-medals-wrapper rcg-block-w',
                            append: $('<div/>', {
                                class: 'rcg-pr-medals-title rcg-block-h',
                                text: messageBundle['awards'],
                                append: $('<i/>', {
                                    class: 'fa fa-info-circle rcg-tooltip',
                                    title: messageBundle['employee.awards']
                                })
                            }).add($medalsBodyTeam)
                        }).appendTo($rightBlock);
                        $(window).on('resize', function () {
                            calculateMedalBody(undefined);
                        });
                        loadPersonMedalsWithCurrentProfile();

                        function calculateMedalBody($moreLink) {
                            if ($moreLink === undefined) {
                                $moreLink = $medalsBodyTeam.find('.rcg-pr-medals-more');
                            }
                            var allIn = $moreLink.attr('all-in');
                            if (allIn === '0') {
                                var $medals = $medalsBodyTeam.find('.rcg-medal-w');
                                var bodyWidth = $medalsBodyTeam.width(),
                                    availableSize = bodyWidth / 75,
                                    totalSize = $medals.length;
                                $.each($medals, function (k, item) {
                                    if (k < availableSize - 1) {
                                        $(item).show();
                                    } else {
                                        $(item).hide();
                                    }
                                });
                                if (totalSize > availableSize) {
                                    $moreLink.show();
                                } else {
                                    $moreLink.hide();
                                }
                            }
                        }

                        function loadPersonMedalsWithCurrentProfile() {
                            $.ajax({
                                'url': webAppUrl + "/rest/v2/services/tsadv_RecognitionService/loadPersonMedals",
                                data: {
                                    personGroupId: currentProfilePojo.pgId
                                },
                                'dataType': 'json',
                                'beforeSend': function (request) {
                                    $medalsBodyTeam.empty();
                                    $medalsWrapperTeam.addClass('rcg-pr-medals-loading');
                                    request.setRequestHeader("Authorization", "Bearer " + accessToken);
                                    request.setRequestHeader("Accept-Language", language);
                                }
                            }).done(function (data, textStatus, jqXHR) {
                                if (data && data.length > 0) {
                                    if ($medalsWrapperTeam.hasClass('rcg-pr-medals-empty')) {
                                        $medalsWrapperTeam.removeClass('rcg-pr-medals-empty');
                                    }
                                    $.each(data, function (i, item) {
                                        $medalsBodyTeam.append(makeMedal(item));
                                    });
                                    var $moreLink = $('<span/>', {
                                        class: 'rcg-pr-medals-more',
                                        text: messageBundle['more'],
                                        'all-in': '0',
                                        append: $('<i/>', {
                                            class: 'fa fa-chevron-down'
                                        }),
                                        click: function () {
                                            var _self = $(this), icon = _self.find('i');
                                            var allIn = _self.attr('all-in');
                                            if (allIn === undefined || !allIn) {
                                                allIn = '0';
                                            }
                                            _self.attr('all-in', Math.abs(Number(allIn) - 1));
                                            if (allIn === '0') {
                                                $medalsBodyTeam.find('.rcg-medal-w').show();
                                                icon.removeClass('fa-chevron-down').addClass('fa-chevron-up');
                                                setTextContents($moreLink, messageBundle['hide']);
                                            } else {
                                                icon.removeClass('fa-chevron-up').addClass('fa-chevron-down');
                                                setTextContents($moreLink, messageBundle['more']);
                                                calculateMedalBody($moreLink);
                                            }
                                        }
                                    });
                                    $moreLink.hide();
                                    $medalsBodyTeam.append($moreLink);
                                    calculateMedalBody($moreLink);
                                }
                            }).fail(function (jqXHR, textStatus, error) {
                                $medalsWrapperTeam.addClass('rcg-pr-medals-empty');
                                $('<label/>', {
                                    class: 'rcg-list-empty-label',
                                    text: jqXHR.responseText
                                }).appendTo($medalsWrapperTeam);
                            }).always(function () {
                                $medalsWrapperTeam.removeClass('rcg-pr-medals-loading');
                            });

                            function setTextContents($elem, text) {
                                $elem.contents().filter(function () {
                                    if (this.nodeType === Node.TEXT_NODE) {
                                        this.nodeValue = text;
                                    }
                                });
                            }

                            function makeMedal(item) {
                                var count = item.count;
                                return $('<div/>', {
                                    class: 'rcg-medal-w',
                                    append: $('<img/>', {
                                        src: item.medalImage,
                                        class: 'rcg-medal-image rcg-tooltip',
                                        title: item.medalName
                                    }).add($('<span/>', {
                                        class: 'rcg-medal-count ' + ((count && count === 1) ? 'fa fa-check' : ''),
                                        text: (count && count > 1) ? count : ''
                                    }))
                                });
                            }
                        }
                    }
                } else if (pageName === 'profile' && wallType == 1) {
                    if (currentProfilePojo.medalCount && currentProfilePojo.medalCount > 0) {
                        //medals block
                        var $medalsBodyTeam = $('<div/>', {
                            class: 'rcg-pr-medals-body rcg-block-b clearfix'
                        });
                        var $medalsWrapperTeam = $('<div/>', {
                            class: 'rcg-pr-medals-wrapper rcg-block-w',
                            append: $('<div/>', {
                                class: 'rcg-pr-medals-title rcg-block-h',
                                text: messageBundle['awards'],
                                append: $('<i/>', {
                                    class: 'fa fa-info-circle rcg-tooltip',
                                    title: messageBundle['employee.awards']
                                })
                            }).add($medalsBodyTeam)
                        }).appendTo($rightBlock);
                        $(window).on('resize', function () {
                            calculateMedalBody(undefined);
                        });
                        loadPersonMedalsWithProfile();

                        function calculateMedalBody($moreLink) {
                            if ($moreLink === undefined) {
                                $moreLink = $medalsBodyTeam.find('.rcg-pr-medals-more');
                            }
                            var allIn = $moreLink.attr('all-in');
                            if (allIn === '0') {
                                var $medals = $medalsBodyTeam.find('.rcg-medal-w');
                                var bodyWidth = $medalsBodyTeam.width(),
                                    availableSize = bodyWidth / 75,
                                    totalSize = $medals.length;
                                $.each($medals, function (k, item) {
                                    if (k < availableSize - 1) {
                                        $(item).show();
                                    } else {
                                        $(item).hide();
                                    }
                                });
                                if (totalSize > availableSize) {
                                    $moreLink.show();
                                } else {
                                    $moreLink.hide();
                                }
                            }
                        }

                        function loadPersonMedalsWithProfile() {
                            $.ajax({
                                'url': webAppUrl + "/rest/v2/services/tsadv_RecognitionService/loadPersonMedals",
                                data: {
                                    personGroupId: profilePojo.pgId
                                },
                                'dataType': 'json',
                                'beforeSend': function (request) {
                                    $medalsBodyTeam.empty();
                                    $medalsWrapperTeam.addClass('rcg-pr-medals-loading');
                                    request.setRequestHeader("Authorization", "Bearer " + accessToken);
                                    request.setRequestHeader("Accept-Language", language);
                                }
                            }).done(function (data, textStatus, jqXHR) {
                                if (data && data.length > 0) {
                                    if ($medalsWrapperTeam.hasClass('rcg-pr-medals-empty')) {
                                        $medalsWrapperTeam.removeClass('rcg-pr-medals-empty');
                                    }
                                    $.each(data, function (i, item) {
                                        $medalsBodyTeam.append(makeMedal(item));
                                    });
                                    var $moreLink = $('<span/>', {
                                        class: 'rcg-pr-medals-more',
                                        text: messageBundle['more'],
                                        'all-in': '0',
                                        append: $('<i/>', {
                                            class: 'fa fa-chevron-down'
                                        }),
                                        click: function () {
                                            var _self = $(this), icon = _self.find('i');
                                            var allIn = _self.attr('all-in');
                                            if (allIn === undefined || !allIn) {
                                                allIn = '0';
                                            }
                                            _self.attr('all-in', Math.abs(Number(allIn) - 1));
                                            if (allIn === '0') {
                                                $medalsBodyTeam.find('.rcg-medal-w').show();
                                                icon.removeClass('fa-chevron-down').addClass('fa-chevron-up');
                                                setTextContents($moreLink, messageBundle['hide']);
                                            } else {
                                                icon.removeClass('fa-chevron-up').addClass('fa-chevron-down');
                                                setTextContents($moreLink, messageBundle['more']);
                                                calculateMedalBody($moreLink);
                                            }
                                        }
                                    });
                                    $moreLink.hide();
                                    $medalsBodyTeam.append($moreLink);
                                    calculateMedalBody($moreLink);
                                }
                            }).fail(function (jqXHR, textStatus, error) {
                                $medalsWrapperTeam.addClass('rcg-pr-medals-empty');
                                $('<label/>', {
                                    class: 'rcg-list-empty-label',
                                    text: jqXHR.responseText
                                }).appendTo($medalsWrapperTeam);
                            }).always(function () {
                                $medalsWrapperTeam.removeClass('rcg-pr-medals-loading');
                            });

                            function setTextContents($elem, text) {
                                $elem.contents().filter(function () {
                                    if (this.nodeType === Node.TEXT_NODE) {
                                        this.nodeValue = text;
                                    }
                                });
                            }

                            function makeMedal(item) {
                                var count = item.count;
                                return $('<div/>', {
                                    class: 'rcg-medal-w',
                                    append: $('<img/>', {
                                        src: item.medalImage,
                                        class: 'rcg-medal-image rcg-tooltip',
                                        title: item.medalName
                                    }).add($('<span/>', {
                                        class: 'rcg-medal-count ' + ((count && count === 1) ? 'fa fa-check' : ''),
                                        text: (count && count > 1) ? count : ''
                                    }))
                                });
                            }
                        }
                    }
                }

            }

            if (profilePojo.inTeam === 1 || wallType === 0) {
                $links.append($('<li/>', {
                    append: $('<a/>', {
                        href: '#',
                        text: messageBundle['team.analytics'],
                        click: function () {
                            connector.showAnalytics(profilePojo.pgId);
                        }
                    })
                }));

                $links.append(createTab(messageBundle['rcg.feedback.wt'], function () {
                    currentTab = 'RECEIVE';
                    resetLink(this);
                    loadRcgFeedbackTab();
                }));
            }

            if (profilePojo.inTeam === 1 || wallType === 0) {
                $links.append(createTab('Wishlist', function () {
                    resetLink(this);
                    loadWishListTab();
                }, false));
            }

            var $rcgTypesWrapper = $('<div/>', {
                class: 'rcg-pr-thanks-wrap'
            }).appendTo($profileWrapper);

            var rcgTypes = profilePojo.recognitionTypes;
            if (rcgTypes) {
                $.each(rcgTypes, function (k, type) {
                    $rcgTypesWrapper.append($('<div/>', {
                        class: 'rcg-pr-thanks-item',
                        'rcg-type-id': type.id,
                        append: $('<img/>', {
                            class: 'rcg-pr-thanks-image',
                            src: type.image
                        }).add($('<div/>', {
                            class: 'rcg-pr-thanks-count',
                            text: type.count
                        })).add($('<div/>', {
                            class: 'rcg-pr-thanks-type',
                            text: type.name
                        }))
                    }));
                });
            }

            var $preferenceHeader = $('<div/>', {
                class: 'rcg-pr-preference-h',
                append: $('<span/>', {
                    class: 'rcg-pr-preference-bn',
                    text: messageBundle['preferences']
                })
            });
            if (wallType === 0) {
                $preferenceHeader.append($('<a/>', {
                    href: '#',
                    class: 'rcg-pr-preference-edit rcg-reset-link rcg-tooltip',
                    text: messageBundle['edit'],
                    title: messageBundle['edit'],
                    click: function () {
                        connector.changePreference();
                    }
                }));
            } else {
                var showO = messageBundle['show.original'],
                    showT = messageBundle['show.translated'],
                    translated = state.automaticTranslate,
                    $translator = $('<a/>', {
                        class: 'rcg-pr-preference-edit rcg-reset-link rcg-tooltip',
                        'translated': '0',
                        'translate-link': translated === 1 ? '0' : '1',
                        text: translated === 1 ? showO : showT,
                        click: function () {
                            var _self = $(this),
                                translated = Number(_self.attr('translated')),
                                translateLink = Number(_self.attr('translate-link'));

                            var preferences = $preferenceWrapper.find('.rcg-pr-preferences');
                            if (preferences) {
                                $.each(preferences, function (k, v) {
                                    var _self = $(v);

                                    _self.text(_self.attr(translated === 0 ? 'reverse-text' : 'display-text'));
                                });
                            }

                            translateLink = Math.abs(translateLink - 1);
                            _self.text(translateLink === 1 ? showT : showO);

                            _self.attr({
                                'translated': Math.abs(translated - 1),
                                'translate-link': translateLink
                            });
                        }
                    });
                $preferenceHeader.append($translator);
            }

            var $preferenceWrapper = $('<div/>', {
                class: 'rcg-pr-preference-wrap',
                append: $preferenceHeader
            }).appendTo($profileWrapper);

            loadPreferences();

            reloadPreferences = function () {
                $preferenceWrapper.find('.rcg-pr-preference-item').remove();
                loadPreferences()
            };

            function loadPreferences() {
                $.each(profilePojo.preferences, function (k, preference) {
                    $('<div/>', {
                        class: 'rcg-pr-preference-item',
                        append: $('<div/>', {
                            class: 'rcg-pr-preference-type',
                            text: preference.typeName
                        }).add($('<div/>', {
                            class: 'rcg-pr-preferences',
                            'reverse-text': preference.reverseText,
                            'display-text': preference.description,
                            text: preference.description
                        }))
                    }).appendTo($preferenceWrapper);
                });
            }

            $leftBlock.append($profileWrapper);

            /*$leftBlock.append($('<div/>', {
                id: 'rf-chart-w',
                class: 'rcg-block rf-chart-w rf-in-profile',
                append: $('<div/>', {
                    class: 'rf-chart-title',
                    text: 'Как оценили ' + profilePojo.fullName
                }).add($('<div/>', {
                    id: 'rf-chart',
                    class: 'rf-chart'
                }))
            }));*/

            loadFeedbackChart();
            loadBanners('profile', $leftBlock);

            $rightBlock.append($rcgWallWrapper);
        }

        $rcgWallWrapperHeader.append($links);

        $row.append($leftBlock);
        $row.append($rightBlock);

        var $win = $('.rcg-content-wrapper');

        $rcgWallWrapper.append($('<div/>', {
            class: 'rcg-list-loader'
        }));

        if (pageName === 'feedback') {
            currentTab = 'RECEIVE';
            loadRcgFeedbackTab();
        } else {
            loadThanksTab();
        }

        function feedbackCount(tabIndex, spanCountChange) {
            $.ajax({
                'url': webAppUrl + "/rest/v2/services/tsadv_RcgFeedbackService/feedbackCount",
                data: {
                    type: tabIndex,
                    personGroupId: profilePojo.pgId,
                    filter: ""
                },
                'dataType': 'json',
                'beforeSend': function (request) {
                    request.setRequestHeader("Authorization", "Bearer " + accessToken);
                    request.setRequestHeader("Accept-Language", language);
                }
            }).done(function (data, textStatus, jqXHR) {
                spanCountChange(data);
            }).fail(function (jqXHR, textStatus, error) {
                //
            });
        }

        function loadRcgFeedbackTab() {
            var $rcgList = $('<div class="rcg-list"/>');
            currentPage = recordsCount = pagesCount = 0;

            var $searchInput = $('<input/>', {
                type: 'text',
                class: 'rcg-search-input',
                keypress: function (e) {
                    if (e.keyCode === 13) {
                        e.preventDefault();

                        currentPage = recordsCount = pagesCount = 0;
                        $rcgList.empty();

                        loadFeedback();
                    }
                }
            });

            // init search form
            var $searchForm = $('<div/>', {
                class: 'rcg-pr-search-form',
                append: $('<div/>', {
                    class: 'rcg-pr-search-input-wrap',
                    append: $('<i/>', {
                        class: 'fa fa-search'
                    }).add($('<i/>', {
                        class: 'fa fa-times',
                        click: function () {
                            var searchText = $searchInput.val();
                            if (searchText) {
                                $searchInput.val('');
                                currentPage = recordsCount = pagesCount = 0;
                                $rcgList.empty();

                                loadFeedback();
                            }
                        }
                    })).add($searchInput)
                })
            });

            $rcgWall.append($searchForm);
            $rcgWall.append($rcgList);

            $win.unbind('refreshFeedbackEvent')
                .bind('refreshFeedbackEvent', function (e) {
                    currentPage = recordsCount = pagesCount = 0;
                    $rcgList.empty();

                    refreshTabCounter(currentTab === 'REQUEST' ? 2 : null);
                    loadFeedback();
                });

            $win.unbind('reloadFeedbackEvent')
                .bind('reloadFeedbackEvent', function (e) {
                    var feedback = $.parseJSON(e.feedback),
                        $card = $rcgList.find(".rcg-wrapper[rf-id='" + feedback.id + "']");
                    if ($card) {
                        $card.find('.rf-theme-w span').text(feedback.theme);
                        $card.find('.rcg-type-image > img').attr({
                            'src': feedback.type.image,
                            title: feedback.type.name
                        });

                        var $comment = $card.find('.rcg-body-text-comment');
                        $comment.text(feedback.comment);
                        $comment.attr('reverse-text', feedback.reverseComment);

                        if (feedback.attachmentChanged) {
                            makeFeedbackAttachments($card.find('.rcg-body-text'), feedback.attachments);
                        }

                        initTooltip();
                    }
                });

            loadingFunction = function () {
                loadFeedback();
            };

            loadFeedback();

            if (pageName !== 'profile' && currentTab !== 'REQUEST') {
                loadFeedbackChart();
            }

            function loadFeedback() {
                var init = currentPage === 0,
                    searchText = $searchInput.val();

                currentPage++;

                $.ajax({
                    'url': webAppUrl + "/rest/v2/services/tsadv_RcgFeedbackService/loadFeedback",
                    data: {
                        page: currentPage,
                        lastCount: recordsCount,
                        type: currentTab === 'RECEIVE' ? 0 : currentTab === 'SEND' ? 1 : 2,
                        personGroupId: profilePojo.pgId,
                        filter: searchText
                    },
                    'dataType': 'json',
                    'beforeSend': function (request) {
                        $rcgWallWrapper.addClass('rcg-list-loading');

                        if (init) {
                            $rcgWall.addClass('rcg-wall-init');
                        }

                        request.setRequestHeader("Authorization", "Bearer " + accessToken);
                        request.setRequestHeader("Accept-Language", language);
                    }
                }).done(function (data, textStatus, jqXHR) {
                    if (data) {
                        if ($rcgList.hasClass('rcg-list-empty')) {
                            $rcgList.removeClass('rcg-list-empty');
                        }

                        pagesCount = Number(data.pageInfo.pagesCount);
                        recordsCount = Number(data.pageInfo.totalRowsCount);

                        var feedbackItems = data.feedback;

                        if (recordsCount > 0 && (feedbackItems && feedbackItems.length > 0)) {
                            $.each(feedbackItems, function (i, item) {
                                $rcgList.append(makeFeedback(item));
                            });
                        } else {
                            if (init) {
                                $rcgList.addClass('rcg-list-empty');

                                $('<label/>', {
                                    class: 'rcg-list-empty-label',
                                    text: messageBundle['there.nothing']
                                }).appendTo($rcgList);
                            }
                        }

                        initTooltip();
                    }
                }).fail(function (jqXHR, textStatus, error) {
                    $rcgList.addClass('rcg-list-empty');

                    $('<label/>', {
                        class: 'rcg-list-empty-label',
                        text: jqXHR.responseText
                    }).appendTo($rcgList);
                }).always(function () {
                    loadingOnScroll = false;
                    $rcgWallWrapper.removeClass('rcg-list-loading');

                    if (init) {
                        $rcgWall.removeClass('rcg-wall-init');
                    }
                });
            }

            function makeFeedback(item) {
                var $div = $('<div/>', {
                    class: 'rcg-wrapper rcg-block',
                    'rf-id': item.id
                });

                $div.append($('<div/>', {
                    class: 'rf-theme-w',
                    append: $('<span/>', {
                        text: item.theme
                    })
                }));

                if (profilePojo.pgId === item.sender.id) {
                    $div.find('.rf-theme-w')
                        .append($('<div>', {
                            class: 'rf-setting',
                            append: $('<a>', {
                                class: 'rcg-reset-link',
                                text: '...',
                                click: function () {
                                    connector.editFeedback(item.id);
                                }
                            })
                        }));
                }

                var $header = $('<div>', {
                    class: 'rcg-header'
                }).appendTo($div);

                $('<div>', {
                    class: 'rcg-sender-image',
                    append: $('<img>', {
                        src: item.sender.image
                    })
                }).appendTo($header);

                var $personNameLink = $('<a>', {
                    class: 'rcg-sender rcg-reset-link',
                    text: (currentTab !== 'SEND' && !(currentTab === 'REQUEST' && item.forMe)) ? item.sender.name : item.receiver.name,
                    click: function () {
                        connector.openProfilePage((currentTab !== 'SEND' && !(currentTab === 'REQUEST' && item.forMe)) ? item.sender.id : item.receiver.id);
                    }
                });

                var $say = $('<span>', {
                    class: 'rcg-type',
                    text: item.say
                });

                var $wallAuthors;
                if (currentTab === 'REQUEST' && item.forMe) {
                    $wallAuthors = $('<div>', {
                        class: 'rcg-authors',
                        append: $say
                            .add($personNameLink)
                    });
                } else {
                    $wallAuthors = $('<div>', {
                        class: 'rcg-authors',
                        append: $personNameLink
                            .add($say)
                    });
                }

                if (currentTab !== 'REQUEST' && !item.forMe) {
                    $wallAuthors.append($('<a>', {
                        class: 'rcg-receiver rcg-reset-link',
                        text: item.receiver.name,
                        click: function () {
                            connector.openProfilePage(item.receiver.id);
                        }
                    }));
                }

                $('<div>', {
                    class: 'rcg-info',
                    append: $wallAuthors.add($('<div>', {
                        class: 'rcg-setting',
                        append: $('<span>', {
                            class: 'rcg-date',
                            text: item.createDate
                        })
                    }))
                }).appendTo($header);

                var $rcgBodyTextComment = $('<div/>', {
                    class: 'rcg-body-text-comment',
                    'reverse-text': item.reverseComment,
                    text: item.comment
                });

                var $rcgBodyWrapper = $('<div>', {
                    class: 'rcg-body-text',
                    append: $rcgBodyTextComment
                });

                var showO = messageBundle['show.original'],
                    showT = messageBundle['show.translated'],
                    $translator = $('<a/>', {
                        class: 'rcg-translate-link',
                        'translated': '0',
                        'translate-link': item.translated === 1 ? '0' : '1',
                        text: item.translated === 1 ? showO : showT,
                        click: function () {
                            var _self = $(this),
                                translated = Number(_self.attr('translated')),
                                translateLink = Number(_self.attr('translate-link'));

                            $rcgBodyTextComment.text(translated === 0 ? $rcgBodyTextComment.attr('reverse-text') : item.text);

                            translateLink = Math.abs(translateLink - 1);
                            _self.text(translateLink === 1 ? showT : showO);

                            _self.attr({
                                'translated': Math.abs(translated - 1),
                                'translate-link': translateLink
                            });
                        }
                    });

                $rcgBodyWrapper.append($translator);

                var attachments = item.attachments;
                if (attachments) {
                    makeFeedbackAttachments($rcgBodyWrapper, attachments);
                }

                var $typeW;
                if (item.type) {
                    $typeW = $('<div>', {
                        class: 'rcg-type-image',
                        append: $('<img>', {
                            class: 'rcg-tooltip',
                            src: item.type.image,
                            title: item.type.name
                        })
                    });
                }

                var $rfBody = $('<div>', {
                    class: 'rcg-body'
                });

                if ($typeW) {
                    $rfBody.append($typeW);
                }
                $rfBody.append($rcgBodyWrapper);
                $div.append($rfBody);

                var $commentComponents = $('<a>', {
                    class: 'rcg-comment-link rcg-reset-link',
                    text: messageBundle['comment'],
                    prepend: $('<i/>', {
                        class: 'fa fa-comments-o'
                    }),
                    click: function (e) {
                        var $commForm = $(".rcg-comment-form[rf-id='" + item.id + "']");
                        $commForm.find('textarea.rcg-comment-form-text').focus();

                        if (!isScrolledIntoView($commForm)) {
                            var h = $('.v-slot-rcg-content-wrapper').height() / 2, top = 150 - h;
                            $win.scrollTo($commForm, 500, {
                                offset: {
                                    top: top,
                                    left: 0
                                }
                            });
                        }
                    }
                });

                if (item.sendFeedbackToAuthor) {
                    $commentComponents = $commentComponents.add($('<a>', {
                        href: '#',
                        class: 'rcg-feedback-send-btn rcg-btn rcg-blue-btn rcg-btn-md float-right',
                        text: ' ' + messageBundle['rcg.feedback.send.btn'],
                        prepend: $('<i/>', {
                            class: 'fa fa-comments'
                        }),
                        click: function () {
                            connector.sendFeedbackToAuthor(item.id, item.sender.personGroupId);
                        }
                    }));
                }

                $('<div>', {
                    class: 'rcg-like-comment',
                    append: $commentComponents
                }).appendTo($div);

                if (Number(item.commentCount) > 2) {
                    var smCommText = messageBundle['show.more.comment'], hideComm = messageBundle['hide.comment'];
                    $('<a>', {
                        class: 'rcg-another-comments rcg-reset-link',
                        'rf-id': item.id,
                        text: smCommText,
                        'rcg-comment-page': 1,
                        'rcg-comment-pages': item.commentPages,
                        'rcg-comment-load': 1,
                        append: $('<span class="rcg-wall-loader"/>'),
                        click: function (e) {
                            var pages = Number($(this).attr('rcg-comment-pages')),
                                commentPage = Number($(this).attr('rcg-comment-page')),
                                isLoad = Number($(this).attr('rcg-comment-load')),
                                _self = $(this);
                            if (isLoad === 1) {
                                if (commentPage < pages) {
                                    commentPage++;
                                    _self.attr('rcg-comment-page', commentPage);
                                    loadFeedbackComments(item.id, commentPage);
                                    if (commentPage === pages) {
                                        _self.text(hideComm);
                                        _self.attr('rcg-comment-load', 0);
                                    }
                                }
                            } else if (isLoad <= 0) {
                                var $elements = $(".rcg-comment-list[rf-id=" + item.id + "] .rcg-comment:not([rcg-comment-page='1'])");
                                if ($elements) {
                                    if (isLoad === 0) {
                                        $elements.hide(100);
                                        _self.text(smCommText);
                                        _self.attr('rcg-comment-load', -1);
                                    } else {
                                        $elements.show(300);
                                        _self.text(hideComm);
                                        _self.attr('rcg-comment-load', 0);
                                    }
                                }
                            }
                        }
                    }).appendTo($div);
                }

                var $commentWrapper = $('<div>', {
                    class: 'rcg-comment-list',
                    'rf-id': item.id
                }).appendTo($div);

                var comments = item.lastComments;
                if (comments) {
                    $commentWrapper.attr('rcg-comment-page', 1);
                    $.each(comments, function (i, comment) {
                        $commentWrapper.prepend(makeFeedbackComment(item.id, comment, 1));
                    });
                }

                var $textarea = $('<textarea/>', {
                    class: 'rcg-comment-form-text',
                    placeholder: messageBundle['write.comment'],
                    tabindex: TAB_INDEX++,
                    focus: function () {
                        if (!$commForm.hasClass('focusing')) {
                            $commForm.addClass('focusing');
                        }
                    }
                });

                var $commForm = $('<div/>', {
                    class: 'rcg-comment-form',
                    'rf-id': item.id,
                    append: $('<div/>', {
                        class: 'rcg-comment-form-author',
                        append: $('<img/>', {
                            src: currentProfilePojo.image
                        })
                    }).add($('<div/>', {
                        class: 'row rcg-comment-form-row',
                        append: $('<div/>', {
                            class: 'rcg-comment-form-l',
                            append: $textarea.add('<span/>', {
                                class: 'comment-send-loader'
                            })
                        }).add($('<div/>', {
                            class: 'rcg-comment-form-r',
                            append: $('<a/>', {
                                class: 'rcg-comment-send rcg-btn rcg-blue-btn rcg-btn-sm',
                                text: messageBundle['send'],
                                tabindex: TAB_INDEX++,
                                href: "#",
                                prepend: $('<i class="fa fa-paper-plane"/>'),
                                click: function (e) {
                                    var commText = $textarea.val();
                                    if (commText && commText !== '') {
                                        prepareFeedbackSendComment(item.id, commText);
                                    } else {
                                        alert(messageBundle['fill.field'])
                                    }
                                }
                            }).add($('<a/>', {
                                class: 'rcg-comment-reset rcg-reset-link',
                                text: messageBundle['cancel'],
                                tabindex: TAB_INDEX++,
                                href: "#",
                                click: function (e) {
                                    $textarea.val(null);
                                    $commForm.removeClass('focusing');
                                    $commForm.removeAttr('parent-comment-id');

                                    var $findCommReceiver = $commForm.find('.rcg-comment-receiver-wrapper');
                                    if ($findCommReceiver) {
                                        $findCommReceiver.remove();
                                    }
                                }
                            }))
                        }))
                    }))
                }).appendTo($div);

                return $div;
            }

            function makeFeedbackAttachments($parent, attachments) {
                var $attachmentsW = $parent.find('.rf-attachments');
                if ($attachmentsW.length === 0) {
                    $attachmentsW = $('<div/>', {
                        class: 'rf-attachments clearfix'
                    });

                    $parent.append($attachmentsW);
                } else {
                    var $lg = $attachmentsW.data('lightGallery');
                    if ($lg !== undefined) {
                        $lg.destroy(true);
                    }
                    $attachmentsW.empty();
                }

                if (attachments && attachments.length > 0) {
                    $.each(attachments, function (k, v) {
                        var $thumbnail = $('<a/>');
                        switch (v.type) {
                            case 'IMAGE': {
                                $thumbnail.attr('data-src', v.url);
                                $thumbnail.append($('<img/>', {
                                    class: 'rf-attachment',
                                    src: v.url
                                }));
                                break;
                            }
                            case 'VIDEO': {
                                $thumbnail.attr({
                                    'data-poster': './VAADIN/themes/base/images/video-icon.png',
                                    'data-html': '#video1',
                                    'v-url': v.url,
                                    'v-type': 'video/mp4'
                                });

                                $thumbnail.append($('<img/>', {
                                    class: 'rf-attachment',
                                    src: './VAADIN/themes/base/images/video-icon.png'
                                }));
                                break;
                            }
                            default: {

                            }
                        }
                        $thumbnail.attr('type', v.type);
                        $attachmentsW.append($thumbnail);
                    });

                    $attachmentsW.lightGallery({
                        slideEndAnimatoin: false,
                        contain: true,
                        speed: 1,
                        videojs: true,
                        pager: false,
                        controls: false,
                        thumbnail: false,
                        share: false,
                        zoom: false,
                        autoplay: false,
                        autoplayFirstVideo: false,
                        autoplayControls: false,
                        download: false
                    });

                    $attachmentsW.off('onBeforeSlide.lg').on('onBeforeSlide.lg', function (e, pi, i, fromTouch, fromThumb) {
                        $parent.find('#video1').remove();

                        var $el = $(this).find('a').eq(i);
                        if ($el.attr('type') === 'VIDEO') {
                            $parent.append($('<div/>', {
                                id: 'video1',
                                append: $('<video/>', {
                                    class: 'lg-video-object lg-html5 video-js vjs-default-skin',
                                    controls: true,
                                    preload: 'auto',
                                    text: 'Your browser does not support HTML5 video.',
                                    append: $('<source/>', {
                                        src: $el.attr('v-url'),
                                        type: $el.attr('v-type')
                                    })
                                })
                            }).css('display', 'none'));
                        }
                    });

                    $attachmentsW.off('onCloseAfter.lg').on('onCloseAfter.lg', function () {
                        $parent.find('#video1').remove();
                    });
                } else {
                    $attachmentsW.remove();
                }
            }

            function prepareFeedbackSendComment(rfId, text) {
                var data = {},
                    $commForm = $('.rcg-comment-form[rf-id=' + rfId + ']');

                data.commentPojo = {};
                data.commentPojo.feedbackId = rfId;
                data.commentPojo.text = text;
                data.commentPojo.parentCommentId = $commForm.attr('parent-comment-id');

                $.ajax({
                    url: webAppUrl + "/rest/v2/services/tsadv_RcgFeedbackService/sendComment",
                    type: "POST",
                    contentType: "application/json",
                    data: JSON.stringify(data),
                    headers: {
                        "Authorization": "Bearer " + accessToken,
                        "Accept-Language": language
                    },
                    beforeSend: function (request) {
                        $commForm.addClass('comment-form-sending');
                    }
                }).done(function (data, textStatus, jqXHR) {
                    if (data) {
                        var parsed_data = JSON.parse(data);

                        if (parsed_data.success === true) {
                            var $commentList = $('.rcg-comment-list[rf-id="' + rfId + '"]');
                            $commentList.append(makeFeedbackComment(rfId, parsed_data.commentPojo, 1));
                        } else {
                            alert(parsed_data.message);
                        }
                    }
                }).fail(function (jqXHR, textStatus, error) {
                    alert('fail:' + jqXHR.responseText);
                }).always(function (res) {
                    //show loader
                    $commForm.find('textarea.rcg-comment-form-text').focus().val('');
                    $commForm.removeClass('comment-form-sending');
                })
            }

            function makeFeedbackComment(rfId, comment, page) {
                var $div = $('<div/>', {
                    class: 'rcg-comment',
                    'rcg-comment-page': page
                });

                $('<div/>', {
                    class: 'rcg-comment-author-image',
                    append: $('<img/>', {
                        src: comment.author.image
                    })
                }).appendTo($div);

                var showO = messageBundle['show.original'],
                    showT = messageBundle['show.translated'],
                    $commentText = $('<span/>', {
                        'reverse-text': comment.reverseText,
                        text: comment.text
                    }),
                    $translator = $('<a/>', {
                        class: 'rcg-comment-translate-link',
                        'translated': '0',
                        'translate-link': comment.translated === 1 ? '0' : '1',
                        text: comment.translated === 1 ? showO : showT,
                        click: function () {
                            var _self = $(this),
                                translated = Number(_self.attr('translated')),
                                translateLink = Number(_self.attr('translate-link'));

                            $commentText.text(translated === 0 ? $commentText.attr('reverse-text') : comment.text);

                            translateLink = Math.abs(translateLink - 1);
                            _self.text(translateLink === 1 ? showT : showO);

                            _self.attr({
                                'translated': Math.abs(translated - 1),
                                'translate-link': translateLink
                            });
                        }
                    });

                $('<div/>', {
                    class: 'rcg-comment-body',
                    append: $('<div/>', {
                        class: 'rcg-comment-h',
                        append: $('<div/>', {
                            class: 'rcg-comment-info',
                            append: $('<a/>', {
                                class: 'rcg-comment-author rcg-reset-link',
                                text: comment.author.name
                            }).add('<br/>').add($('<span/>', {
                                class: 'rcg-comment-date',
                                text: comment.createDate
                            }))
                        })
                    }).add($('<div/>', {
                        class: 'rcg-comment-text',
                        append: $commentText
                    })).add($translator)
                }).appendTo($div);

                return $div;
            }

            function loadFeedbackComments(fbId, page) {
                var $rcgLoadLink = $('.rcg-another-comments[rf-id=' + fbId + ']');

                $.ajax({
                    'url': webAppUrl + '/rest/v2/services/tsadv_RcgFeedbackService/loadFeedbackComments?feedbackId=' + fbId + '&page=' + page,
                    'dataType': 'json',
                    'beforeSend': function (request) {
                        $rcgLoadLink.addClass('loading-another-comments');

                        request.setRequestHeader("Authorization", "Bearer " + accessToken);
                        request.setRequestHeader("Accept-Language", language);
                    }
                }).done(function (data, textStatus, jqXHR) {
                    if (data) {
                        var $commentList = $('.rcg-comment-list[rf-id="' + fbId + '"]');
                        $.each(data, function (i, item) {
                            $commentList.prepend(makeFeedbackComment(fbId, item, page));
                        });
                    }
                }).always(function () {
                    $rcgLoadLink.removeClass('loading-another-comments');
                });
            }
        }

        function loadFeedbackChart() {
            $.ajax({
                'url': webAppUrl + "/rest/v2/services/tsadv_RcgFeedbackService/loadChartData",
                data: {
                    personGroupId: profilePojo.pgId,
                    direction: currentTab
                },
                'dataType': 'json',
                'beforeSend': function (request) {
                    request.setRequestHeader("Authorization", "Bearer " + accessToken);
                    request.setRequestHeader("Accept-Language", language);
                }
            }).done(function (chartData, textStatus, jqXHR) {
                if (chartData) {
                    var colorsArray = [];
                    for (var i = 0; i < chartData.length; i++) {
                        colorsArray.push(chartData[i].color);
                    }
                    renderChart(chartData, colorsArray);
                }
            }).fail(function (jqXHR, textStatus, error) {
                //jqXHR.responseText

            }).always(function () {

            });

            function renderChart(chartData, colorsArray) {
                AmCharts.makeChart("rf-chart", {
                    "type": "pie",
                    "theme": "none",
                    "dataProvider": chartData,
                    "labelText": "",
                    "valueField": "cnt",
                    "titleField": "type",
                    "balloon": {
                        "fixedPosition": true
                    },
                    "legend": {
                        "enabled": true,
                        "position": "bottom",
                        "align": "center",
                        "markerSize": 10
                    },
                    "colors": colorsArray,
                    "export": {
                        "enabled": true
                    }
                });
            }
        }

        function loadWishListTab() {
            var $goodsList = $('<div class="row rcg-wish-list"/>');
            currentPage = recordsCount = pagesCount = 0;

            $rcgWall.append($goodsList);

            loadingFunction = function () {
                loadWishList();
            };

            loadWishList();

            function loadWishList() {
                var init = currentPage === 0;
                currentPage++;

                $.ajax({
                    'url': webAppUrl + "/rest/v2/services/tsadv_RecognitionService/loadWishList",
                    data: {
                        page: currentPage,
                        lastCount: recordsCount,
                        personGroupId: profilePojo.pgId
                    },
                    'dataType': 'json',
                    'beforeSend': function (request) {
                        $rcgWallWrapper.addClass('rcg-list-loading');

                        if (init) {
                            $rcgWall.addClass('rcg-wall-init');
                        }

                        request.setRequestHeader("Authorization", "Bearer " + accessToken);
                        request.setRequestHeader("Accept-Language", language);
                    }
                }).done(function (data, textStatus, jqXHR) {
                    if (data) {
                        if ($goodsList.hasClass('rcg-list-empty')) {
                            $goodsList.removeClass('rcg-list-empty');
                        }

                        pagesCount = Number(data.pageInfo.pagesCount);
                        recordsCount = Number(data.pageInfo.totalRowsCount);

                        var goods = data.goods;

                        if (recordsCount > 0 && (goods && goods.length > 0)) {
                            $.each(goods, function (i, item) {
                                $goodsList.append(makeWishList(item));
                            });
                        } else {
                            if (init) {
                                $goodsList.addClass('rcg-wish-list-empty');

                                $('<label/>', {
                                    class: 'rcg-wish-list-empty-label',
                                    text: messageBundle['there.nothing']
                                }).appendTo($goodsList);
                            }
                        }
                    }
                }).fail(function (jqXHR, textStatus, error) {
                    $goodsList.addClass('rcg-wish-list-empty');

                    $('<label/>', {
                        class: 'rcg-wish-list-empty-label',
                        text: jqXHR.responseText
                    }).appendTo($goodsList);
                }).always(function () {
                    loadingOnScroll = false;
                    $rcgWallWrapper.removeClass('rcg-list-loading');

                    if (init) {
                        $rcgWall.removeClass('rcg-wall-init');
                    }
                });
            }

            function makeWishList(goods) {
                return $('<div/>', {
                    class: 'col-lg-12 rcg-reset-col',
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
                            class: 'col-lg-7 rcg-sh-g2-body',
                            append: $('<div/>', {
                                class: 'rcg-sh-g2-name',
                                text: goods.name
                            }).add($('<div/>', {
                                class: 'rcg-sh-g2-description',
                                text: goods.description
                            }))
                        })).add($('<div/>', {
                            class: 'col-lg-2 rcg-sh-g2-pw',
                            append: $('<div/>', {
                                class: 'rcg-sh-g2-price',
                                text: goods.price,
                                append: $('<span/>', {
                                    text: ' HC'
                                }),
                                prepend: $('<span/>')
                            })
                        }))
                    })
                });
            }

            function makeCoinLog(log) {
                var $target = $('<div/>', {
                    class: 'rcg-log-target'
                });

                var id = log.targetId;

                if (id) {
                    if (log.operationType === 'RECEIPT') {
                        $target.text(messageBundle['from'] + ' ');
                    }

                    $target.append($('<a/>', {
                        href: '#',
                        text: log.target,
                        click: function () {
                            connector.openProfilePage(id);
                        }
                    }))
                } else {
                    $target.text(log.target)
                }

                var $logComment = null;
                if (log.comment) {
                    $logComment = $('<div/>', {
                        class: 'rcg-log-coins-comment',
                        text: log.comment
                    })
                }

                return $('<div/>', {
                    class: 'row rcg-log-wrapper',
                    append: $($('<div/>', {
                        class: 'col-lg-2 col-md-2 col-sm-2 col-xs-2 rcg-reset-col-l',
                        append: $('<div/>', {
                            class: 'rcg-log-coins',
                            text: log.coins,
                            append: $('<span/>', {
                                class: 'rcg-log-coins-label',
                                text: log.coinType
                            })
                        })
                    })).add($('<div/>', {
                        class: 'col-lg-7 col-md-7 col-sm-7 col-xs-7',
                        append: $('<div/>', {
                            class: 'rcg-log-action-type',
                            text: log.actionType
                        }).add($target).add($logComment)
                    })).add($('<div/>', {
                        class: 'col-lg-3  col-md-3 col-sm-3 col-xs-3 rcg-log-date',
                        text: log.date
                    }))
                });
            }
        }

        function loadBanners(page, $block) {
            var $bannerLoader = $('<div/>', {
                    class: 'rcg-banner-loader'
                }),
                $bannerList = $('<div/>', {
                    class: 'rcg-banner-list'
                }),
                $bannerWrapper = $('<div/>', {
                    class: 'rcg-banner-w hidden-xs',
                    append: $bannerList.add($bannerLoader)
                });

            $block.append($bannerWrapper);

            $.ajax({
                'url': webAppUrl + "/rest/v2/services/tsadv_RecognitionService/loadBanners?page=" + page,
                'dataType': 'json',
                'beforeSend': function (request) {
                    request.setRequestHeader("Authorization", "Bearer " + accessToken);
                    request.setRequestHeader("Accept-Language", language);

                    $bannerLoader.show();
                }
            }).done(function (data, textStatus, jqXHR) {
                if (data) {
                    if (data.length > 0) {
                        $.each(data, function (i, item) {
                            $bannerList.append($('<div/>', {
                                class: 'rcg-banner-image-w',
                                append: $('<img/>', {
                                    class: 'rcg-banner-image',
                                    src: './dispatch/rcg_banner_image/' + item
                                })
                            }));
                        });
                    }
                }
            }).fail(function (jqXHR, textStatus, error) {
                $('<label/>', {
                    class: 'rcg-list-empty-label',
                    text: jqXHR.responseText
                }).appendTo($bannerList);
            }).always(function () {
                $bannerLoader.hide();
            });
        }

        function loadCoinLogTab() {
            var $logList = $('<div class="coin-list"/>');
            currentPage = recordsCount = pagesCount = 0;

            // init search form
            var $coinInstruction = $('<div/>', {
                class: 'coin-instruction',
                append: $('<a/>', {
                    class: '',
                    text: messageBundle['how.get.hc'],
                    click: function () {
                        connector.openFaqDetail('HOW_TO_GET_HC');
                    }
                })
            });

            $rcgWall.append($coinInstruction);
            $rcgWall.append($logList);

            loadingFunction = function () {
                loadCoinLogs();
            };

            loadCoinLogs();

            function loadCoinLogs() {
                var init = currentPage === 0;
                currentPage++;

                $.ajax({
                    'url': webAppUrl + "/rest/v2/services/tsadv_RecognitionService/loadCoinLogs",
                    data: {
                        page: currentPage,
                        lastCount: recordsCount
                    },
                    'dataType': 'json',
                    'beforeSend': function (request) {
                        $rcgWallWrapper.addClass('rcg-list-loading');

                        if (init) {
                            $rcgWall.addClass('rcg-wall-init');
                        }

                        request.setRequestHeader("Authorization", "Bearer " + accessToken);
                        request.setRequestHeader("Accept-Language", language);
                    }
                }).done(function (data, textStatus, jqXHR) {
                    if (data) {
                        if ($logList.hasClass('rcg-list-empty')) {
                            $logList.removeClass('rcg-list-empty');
                        }

                        pagesCount = Number(data.pageInfo.pagesCount);
                        recordsCount = Number(data.pageInfo.totalRowsCount);

                        var logs = data.coinLogs;

                        if (recordsCount > 0 && (logs && logs.length > 0)) {
                            $.each(logs, function (i, item) {
                                $logList.append(makeCoinLog(item));
                            });
                        } else {
                            if (init) {
                                $logList.addClass('rcg-list-empty');

                                $('<label/>', {
                                    class: 'rcg-list-empty-label',
                                    text: messageBundle['there.nothing']
                                }).appendTo($logList);
                            }
                        }
                    }
                }).fail(function (jqXHR, textStatus, error) {
                    $logList.addClass('rcg-list-empty');

                    $('<label/>', {
                        class: 'rcg-list-empty-label',
                        text: jqXHR.responseText
                    }).appendTo($logList);
                }).always(function () {
                    loadingOnScroll = false;
                    $rcgWallWrapper.removeClass('rcg-list-loading');

                    if (init) {
                        $rcgWall.removeClass('rcg-wall-init');
                    }
                });
            }

            function makeCoinLog(log) {
                var $target = $('<div/>', {
                    class: 'rcg-log-target'
                });

                var id = log.targetId;

                if (id) {
                    if (log.operationType === 'RECEIPT') {
                        $target.text(messageBundle['from'] + ' ');
                    }

                    $target.append($('<a/>', {
                        href: '#',
                        text: log.target,
                        click: function () {
                            connector.openProfilePage(id);
                        }
                    }))
                } else {
                    $target.text(log.target)
                }

                var $logComment = null;
                if (log.comment) {
                    $logComment = $('<div/>', {
                        class: 'rcg-log-coins-comment',
                        text: log.comment
                    })
                }

                return $('<div/>', {
                    class: 'row rcg-log-wrapper',
                    append: $($('<div/>', {
                        class: 'col-lg-2 col-md-2 col-sm-2 col-xs-2 rcg-reset-col-l',
                        append: $('<div/>', {
                            class: 'rcg-log-coins',
                            text: log.coins,
                            append: $('<span/>', {
                                class: 'rcg-log-coins-label',
                                text: log.coinType
                            })
                        })
                    })).add($('<div/>', {
                        class: 'col-lg-7 col-md-7 col-sm-7 col-xs-7',
                        append: $('<div/>', {
                            class: 'rcg-log-action-type',
                            text: log.actionType
                        }).add($target).add($logComment)
                    })).add($('<div/>', {
                        class: 'col-lg-3  col-md-3 col-sm-3 col-xs-3 rcg-log-date',
                        text: log.date
                    }))
                });
            }
        }

        function loadThanksTab() {
            var $rcgList = $('<div class="rcg-list"/>');
            currentPage = recordsCount = pagesCount = 0;

            var $searchInput = $('<input/>', {
                type: 'text',
                class: 'rcg-search-input',
                keypress: function (e) {
                    if (e.keyCode === 13) {
                        e.preventDefault();

                        currentPage = recordsCount = pagesCount = 0;
                        $rcgList.empty();

                        loadRecognitions();
                    }
                }
            });

            // init search form
            var $searchForm = $('<div/>', {
                class: 'rcg-pr-search-form',
                append: $('<div/>', {
                    class: 'rcg-pr-search-input-wrap',
                    append: $('<i/>', {
                        class: 'fa fa-search'
                    }).add($('<i/>', {
                        class: 'fa fa-times',
                        click: function () {
                            var searchText = $searchInput.val();
                            if (searchText) {
                                $searchInput.val('');
                                currentPage = recordsCount = pagesCount = 0;
                                $rcgList.empty();

                                loadRecognitions();
                            }
                        }
                    })).add($searchInput)
                })
            });

            if (wallType === -1) {
                var $select = $('<select/>', {
                    class: 'rcg-select-organizations',
                    append: $('<option/>', {
                        value: '-1',
                        text: messageBundle['all.organizations']
                    })
                });
                $searchForm.prepend($select);
                $select.SumoSelect({search: true, searchText: messageBundle['search']});
                var $sumo = $select[0].sumo;

                $select.on('change', function () {
                    var selectedOrganization = $(this).val();
                    if (selectedOrganization !== organizationGroupId) {
                        currentPage = recordsCount = pagesCount = 0;
                        organizationGroupId = selectedOrganization;

                        $rcgList.empty();
                        loadRecognitions();
                    }
                });

                $.ajax({
                    'url': webAppUrl + "/rest/v2/services/tsadv_RecognitionService/loadOrganizations",
                    'dataType': 'json',
                    'beforeSend': function (request) {
                        $sumo.disable();
                        $sumo.CaptionCont.addClass('ss-loading');

                        request.setRequestHeader("Authorization", "Bearer " + accessToken);
                        request.setRequestHeader("Accept-Language", language);
                    }
                }).done(function (data, textStatus, jqXHR) {
                    if (data) {
                        $.each(data, function (k, v) {
                            $select.append($('<option/>', {
                                value: v.groupId,
                                text: v.name,
                                title: v.name
                            }));
                        });
                    }
                }).fail(function (jqXHR, textStatus, error) {
                    //jqXHR.responseText
                }).always(function () {
                    $sumo.CaptionCont.removeClass('ss-loading');
                    $sumo.enable();
                    $sumo.reload();

                    tippy('.rcg-pr-search-form .options .opt', {
                        arrow: true,
                        animation: 'shift-away',
                        theme: 'light',
                        zIndex: 99999,
                        flip: false,
                        placement: 'top'
                    });
                });
            }

            $rcgWall.append($searchForm);
            $rcgWall.append($rcgList);

            loadingFunction = function () {
                loadRecognitions();
            };

            refreshRecognitionWall = function () {
                currentPage = recordsCount = pagesCount = 0;
                $rcgList.empty();
                loadRecognitions();
            };

            loadRecognitions();

            function makeRecognition(item) {
                var $div = $('<div class="rcg-wrapper rcg-block"></div>');

                var $header = $('<div>', {
                    class: 'rcg-header'
                }).appendTo($div);

                $('<div>', {
                    class: 'rcg-sender-image',
                    append: $('<img>', {
                        src: item.sender.image
                    })
                }).appendTo($header);

                var $wallAuthors = $('<div>', {
                    class: 'rcg-authors',
                    append: $('<a>', {
                        class: 'rcg-sender rcg-reset-link',
                        text: item.sender.name,
                        click: function () {
                            connector.openProfilePage(item.sender.id);
                        }
                    }).add($('<span>', {
                        class: 'rcg-type',
                        text: item.say
                        /*,
                        append: $('<span/>', {
                            class: 'rcg-type-name',
                            text: ' ' + item.type.name
                        })*/
                    }))
                });

                if (wallType !== 0) {
                    $wallAuthors.append($('<a>', {
                        class: 'rcg-receiver rcg-reset-link',
                        text: item.receiver.name,
                        click: function () {
                            connector.openProfilePage(item.receiver.id);
                        }
                    }));
                }

                $('<div>', {
                    class: 'rcg-info',
                    append: $wallAuthors.add($('<div>', {
                        class: 'rcg-setting',
                        append: $('<span>', {
                            class: 'rcg-date',
                            text: item.createDate
                        })
                    }))
                }).appendTo($header);

                $('<div>', {
                    class: 'rcg-setting',
                    append: $('<a>', {
                        class: 'rcg-reset-link',
                        text: '...'
                    })
                }).appendTo($header);

                var $rcgBodyTextComment = $('<div/>', {
                    class: 'rcg-body-text-comment',
                    'reverse-text': item.reverseText,
                    text: item.text
                });

                var $rcgBodyWrapper = $('<div>', {
                    class: 'rcg-body-text',
                    append: $rcgBodyTextComment
                });

                var showO = messageBundle['show.original'],
                    showT = messageBundle['show.translated'],
                    $translator = $('<a/>', {
                        class: 'rcg-translate-link',
                        'translated': '0',
                        'translate-link': item.translated === 1 ? '0' : '1',
                        text: item.translated === 1 ? showO : showT,
                        click: function () {
                            var _self = $(this),
                                translated = Number(_self.attr('translated')),
                                translateLink = Number(_self.attr('translate-link'));

                            $rcgBodyTextComment.text(translated === 0 ? $rcgBodyTextComment.attr('reverse-text') : item.text);

                            translateLink = Math.abs(translateLink - 1);
                            _self.text(translateLink === 1 ? showT : showO);

                            _self.attr({
                                'translated': Math.abs(translated - 1),
                                'translate-link': translateLink
                            });
                        }
                    });

                $rcgBodyWrapper.append($translator);

                var qualities = item.qualities;
                if (qualities && qualities.length > 0) {
                    var $rcgQualities = $('<div/>', {
                        class: 'rcg-qualities'
                    });

                    $.each(qualities, function (k, v) {
                        $rcgQualities.append($('<span/>', {
                            class: 'rcg-quality-item rcg-tooltip',
                            text: v.name,
                            title: v.description
                        }));
                    });
                    $rcgBodyWrapper.append($rcgQualities);
                }

                $('<div>', {
                    class: 'rcg-body',
                    append: $('<div>', {
                        class: 'rcg-type-image',
                        append: $('<img>', {
                            src: item.type.image
                        })
                    }).add($rcgBodyWrapper)
                }).appendTo($div);

                var teamLiker = item.teamLiker,
                    likeCount = item.likeCount,
                    $likeCountLink = $('<a/>', {
                        href: '#',
                        class: 'rcg-likes-count',
                        text: likeCount + ' ' + messageBundle['person'],
                        click: function () {
                            connector.openLikesDialog(item.id);
                        }
                    }),
                    $teamLikerLT = $('<span/>', {
                        text: ' ' + messageBundle['like']
                    }),
                    $likeCountLinkWrap = $('<span/>', {
                        text: ' ' + messageBundle['like'],
                        prepend: $likeCountLink
                    }),
                    $andMore = $('<span/>', {
                        text: ' ' + messageBundle['and.more'] + ' '
                    }),
                    $likedWrapper = $('<div/>', {
                        class: 'rcg-liked-wrapper'
                    });

                if (teamLiker || likeCount > 0) {
                    if (teamLiker) {
                        $likedWrapper.append($('<a/>', {
                            href: '#',
                            class: 'rcg-team-liker',
                            text: teamLiker.name,
                            click: function () {
                                connector.openProfilePage(teamLiker.id);
                            }
                        }));

                        if (likeCount > 0) {
                            $likedWrapper.append($andMore);
                        } else if (likeCount === 0) {
                            $likedWrapper.append($teamLikerLT);
                        }
                    }

                    if (likeCount > 0) {
                        $likedWrapper.append($likeCountLinkWrap);
                    }
                }

                $div.append($likedWrapper);

                $('<div>', {
                    class: 'rcg-like-comment',
                    append: $('<a>', {
                        class: 'rcg-like-link rcg-reset-link',
                        text: messageBundle['like.2'],
                        'current-like': item.currentLike,
                        click: function () {
                            var _self = $(this),
                                data = {recognitionId: item.id};
                            $.ajax({
                                url: webAppUrl + "/rest/v2/services/tsadv_RecognitionService/sendLike",
                                type: "POST",
                                contentType: "application/json",
                                data: JSON.stringify(data),
                                headers: {
                                    "Authorization": "Bearer " + accessToken,
                                    "Accept-Language": language
                                },
                                beforeSend: function (request) {
                                    //show loader
                                }
                            }).done(function (data, textStatus, jqXHR) {
                                var selfLike = Number(_self.attr('current-like')),
                                    cl = Math.abs(selfLike - 1);
                                _self.attr('current-like', cl);

                                if (cl === 1) {
                                    likeCount++;
                                } else {
                                    likeCount--;
                                }

                                if (likeCount === 0) {
                                    if (!teamLiker) {
                                        $likedWrapper.hide();
                                    } else {
                                        $andMore.detach();
                                        $likeCountLinkWrap.detach();
                                        if (!$.contains($likedWrapper, $teamLikerLT)) {
                                            $likedWrapper.append($teamLikerLT);
                                        }
                                    }
                                } else {
                                    $likedWrapper.show();
                                    $likeCountLink.text(likeCount + ' ' + messageBundle['person']);

                                    if (!$.contains($likedWrapper, $likeCountLinkWrap)) {
                                        if (teamLiker) {
                                            $teamLikerLT.detach();

                                            if (!$.contains($likedWrapper, $andMore)) {
                                                $likedWrapper.append($andMore);
                                            }
                                        }
                                        $likedWrapper.append($likeCountLinkWrap);
                                    }
                                }
                            }).fail(function (jqXHR, textStatus, error) {
                                alert('fail:' + jqXHR.responseText);
                            }).always(function (res) {
                                //hide loader
                            })
                        },
                        prepend: $('<i/>', {
                            class: 'fa fa-thumbs-up'
                        })
                    }).add($('<a>', {
                        class: 'rcg-comment-link rcg-reset-link',
                        text: messageBundle['comment'],
                        prepend: $('<i/>', {
                            class: 'fa fa-comments-o'
                        }),
                        click: function (e) {
                            var $commForm = $(".rcg-comment-form[rcg-id='" + item.id + "']");
                            $commForm.find('textarea.rcg-comment-form-text').focus();

                            if (!isScrolledIntoView($commForm)) {
                                var h = $('.v-slot-rcg-content-wrapper').height() / 2, top = 150 - h;
                                $win.scrollTo($commForm, 500, {
                                    offset: {
                                        top: top,
                                        left: 0
                                    }
                                });
                            }
                        }
                    }))
                }).appendTo($div);

                if (Number(item.commentCount) > 2) {
                    var smCommText = messageBundle['show.more.comment'], hideComm = messageBundle['hide.comment'];
                    $('<a>', {
                        class: 'rcg-another-comments rcg-reset-link',
                        'rcg-id': item.id,
                        text: smCommText,
                        'rcg-comment-page': 1,
                        'rcg-comment-pages': item.commentPages,
                        'rcg-comment-load': 1,
                        append: $('<span class="rcg-wall-loader"/>'),
                        click: function (e) {
                            var pages = Number($(this).attr('rcg-comment-pages')),
                                commentPage = Number($(this).attr('rcg-comment-page')),
                                isLoad = Number($(this).attr('rcg-comment-load')),
                                _self = $(this);
                            if (isLoad === 1) {
                                if (commentPage < pages) {
                                    commentPage++;
                                    _self.attr('rcg-comment-page', commentPage);
                                    loadComments(item.id, commentPage);
                                    if (commentPage === pages) {
                                        _self.text(hideComm);
                                        _self.attr('rcg-comment-load', 0);
                                    }
                                }
                            } else if (isLoad <= 0) {
                                var $elements = $(".rcg-comment-list[rcg-id=" + item.id + "] .rcg-comment:not([rcg-comment-page='1'])");
                                if ($elements) {
                                    if (isLoad === 0) {
                                        $elements.hide(100);
                                        _self.text(smCommText);
                                        _self.attr('rcg-comment-load', -1);
                                    } else {
                                        $elements.show(300);
                                        _self.text(hideComm);
                                        _self.attr('rcg-comment-load', 0);
                                    }
                                }
                            }
                        }
                    }).appendTo($div);
                }

                var $commentWrapper = $('<div>', {
                    class: 'rcg-comment-list',
                    'rcg-id': item.id
                }).appendTo($div);

                var comments = item.lastComments;
                if (comments) {
                    $commentWrapper.attr('rcg-comment-page', 1);
                    $.each(comments, function (i, comment) {
                        $commentWrapper.prepend(makeComment(item.id, comment, 1));
                    });
                }

                var $textarea = $('<textarea/>', {
                    class: 'rcg-comment-form-text',
                    placeholder: messageBundle['write.comment'],
                    tabindex: TAB_INDEX++,
                    focus: function () {
                        if (!$commForm.hasClass('focusing')) {
                            $commForm.addClass('focusing');
                        }
                    }
                });

                var $commForm = $('<div/>', {
                    class: 'rcg-comment-form',
                    'rcg-id': item.id,
                    append: $('<div/>', {
                        class: 'rcg-comment-form-author',
                        append: $('<img/>', {
                            src: currentProfilePojo.image
                        })
                    }).add($('<div/>', {
                        class: 'row rcg-comment-form-row',
                        append: $('<div/>', {
                            class: 'rcg-comment-form-l',
                            append: $textarea.add('<span/>', {
                                class: 'comment-send-loader'
                            })
                        }).add($('<div/>', {
                            class: 'rcg-comment-form-r',
                            append: $('<a/>', {
                                class: 'rcg-comment-send rcg-btn rcg-blue-btn rcg-btn-sm',
                                text: messageBundle['send'],
                                tabindex: TAB_INDEX++,
                                href: "#",
                                prepend: $('<i class="fa fa-paper-plane"/>'),
                                click: function (e) {
                                    var commText = $textarea.val();
                                    if (commText && commText !== '') {
                                        prepareSendComment(item.id, commText);
                                    } else {
                                        alert(messageBundle['fill.field'])
                                    }
                                }
                            }).add($('<a/>', {
                                class: 'rcg-comment-reset rcg-reset-link',
                                text: messageBundle['cancel'],
                                tabindex: TAB_INDEX++,
                                href: "#",
                                click: function (e) {
                                    $textarea.val(null);
                                    $commForm.removeClass('focusing');
                                    $commForm.removeAttr('parent-comment-id');

                                    var $findCommReceiver = $commForm.find('.rcg-comment-receiver-wrapper');
                                    if ($findCommReceiver) {
                                        $findCommReceiver.remove();
                                    }
                                }
                            }))
                        }))
                    }))
                }).appendTo($div);

                return $div;
            }

            function prepareSendComment(rcgId, text) {
                var data = $.extend({}, commentModel),
                    $commForm = $('.rcg-comment-form[rcg-id=' + rcgId + ']');

                data.commentPojo.recognitionId = rcgId;
                data.commentPojo.text = text;
                data.commentPojo.parentCommentId = $commForm.attr('parent-comment-id');

                $.ajax({
                    url: webAppUrl + "/rest/v2/services/tsadv_RecognitionService/sendComment",
                    type: "POST",
                    contentType: "application/json",
                    data: JSON.stringify(data),
                    headers: {
                        "Authorization": "Bearer " + accessToken,
                        "Accept-Language": language
                    },
                    beforeSend: function (request) {
                        $commForm.addClass('comment-form-sending');
                    }
                }).done(function (data, textStatus, jqXHR) {
                    if (data) {
                        var parsed_data = JSON.parse(data);

                        if (parsed_data.success === true) {
                            var $commentList = $('.rcg-comment-list[rcg-id="' + rcgId + '"]');
                            $commentList.append(makeComment(rcgId, parsed_data.commentPojo, 1));
                        } else {
                            alert(parsed_data.message);
                        }
                    }
                }).fail(function (jqXHR, textStatus, error) {
                    alert('fail:' + jqXHR.responseText);
                }).always(function (res) {
                    //show loader
                    $commForm.find('textarea.rcg-comment-form-text').focus().val('');
                    $commForm.removeClass('comment-form-sending');
                })
            }

            function makeComment(rcgId, comment, page) {
                var $div = $('<div/>', {
                    class: 'rcg-comment',
                    'rcg-comment-page': page
                });

                $('<div/>', {
                    class: 'rcg-comment-author-image',
                    append: $('<img/>', {
                        src: comment.author.image
                    })
                }).appendTo($div);

                var showO = messageBundle['show.original'],
                    showT = messageBundle['show.translated'],
                    $commentText = $('<span/>', {
                        'reverse-text': comment.reverseText,
                        text: comment.text
                    }),
                    $translator = $('<a/>', {
                        class: 'rcg-comment-translate-link',
                        'translated': '0',
                        'translate-link': comment.translated === 1 ? '0' : '1',
                        text: comment.translated === 1 ? showO : showT,
                        click: function () {
                            var _self = $(this),
                                translated = Number(_self.attr('translated')),
                                translateLink = Number(_self.attr('translate-link'));

                            $commentText.text(translated === 0 ? $commentText.attr('reverse-text') : comment.text);

                            translateLink = Math.abs(translateLink - 1);
                            _self.text(translateLink === 1 ? showT : showO);

                            _self.attr({
                                'translated': Math.abs(translated - 1),
                                'translate-link': translateLink
                            });
                        }
                    });

                $('<div/>', {
                    class: 'rcg-comment-body',
                    append: $('<div/>', {
                        class: 'rcg-comment-h',
                        append: $('<div/>', {
                            class: 'rcg-comment-info',
                            append: $('<a/>', {
                                class: 'rcg-comment-author rcg-reset-link',
                                text: comment.author.name
                            }).add('<br/>').add($('<span/>', {
                                class: 'rcg-comment-date',
                                text: comment.createDate
                            }))
                        }).add($('<a/>', {
                            class: 'rcg-comment-reply rcg-reset-link',
                            text: messageBundle['answer'],
                            click: function (e) {
                                var $commForm = $('.rcg-comment-form[rcg-id=' + rcgId + ']');
                                $commForm.attr('parent-comment-id', comment.id);
                                if (!$commForm.hasClass('focusing')) {
                                    $commForm.addClass('focusing')
                                }

                                var $findCommReceiver = $commForm.find('.rcg-comment-receiver-wrapper');
                                if ($findCommReceiver) {
                                    $findCommReceiver.remove();
                                }

                                $findCommReceiver = $('<div/>', {
                                    class: 'rcg-comment-receiver-wrapper',
                                    append: $('<span/>', {
                                        class: 'rcg-comment-receiver',
                                        text: comment.author.name,
                                        prepend: $('<i/>', {
                                            class: 'fa fa-reply'
                                        }),
                                        append: $('<i/>', {
                                            class: 'rcg-comment-remove-receiver fa fa-times',
                                            click: function () {
                                                $commForm.removeAttr('parent-comment-id');
                                                $findCommReceiver.remove();
                                            }
                                        })
                                    })
                                }).appendTo($commForm.find('.rcg-comment-form-l'));

                                $commForm.find('textarea.rcg-comment-form-text').focus();

                                if (!isScrolledIntoView($commForm)) {
                                    var h = $('.v-slot-rcg-content-wrapper').height() / 2, top = 150 - h;
                                    $win.scrollTo($commForm, 500, {
                                        offset: {
                                            top: top,
                                            left: 0
                                        }
                                    });
                                }
                            }
                        }))
                    }).add($('<div/>', {
                        class: 'rcg-comment-text',
                        append: $commentText,
                        prepend: function () {
                            if (comment.parentCommentAuthor) {
                                return $('<span/>', {
                                    class: 'rcg-parent-comment-author',
                                    text: '@' + comment.parentCommentAuthor.name + ', '
                                });
                            }
                            return "";
                        }
                    })).add($translator)
                }).appendTo($div);

                return $div;
            }

            function loadRecognitions() {
                var init = currentPage === 0,
                    searchText = $searchInput.val();

                currentPage++;

                $.ajax({
                    'url': webAppUrl + "/rest/v2/services/tsadv_RecognitionService/loadRecognitions",
                    data: {
                        page: currentPage,
                        lastCount: recordsCount,
                        wallType: wallType,
                        personGroupId: profilePojo.pgId,
                        organizationGroupId: organizationGroupId,
                        filter: searchText
                    },
                    'dataType': 'json',
                    'beforeSend': function (request) {
                        $rcgWallWrapper.addClass('rcg-list-loading');

                        if (init) {
                            $rcgWall.addClass('rcg-wall-init');
                        }

                        request.setRequestHeader("Authorization", "Bearer " + accessToken);
                        request.setRequestHeader("Accept-Language", language);
                    }
                }).done(function (data, textStatus, jqXHR) {
                    if (data) {
                        if ($rcgList.hasClass('rcg-list-empty')) {
                            $rcgList.removeClass('rcg-list-empty');
                        }

                        pagesCount = Number(data.pageInfo.pagesCount);
                        recordsCount = Number(data.pageInfo.totalRowsCount);

                        var recognitions = data.recognitions;

                        if (recordsCount > 0 && (recognitions && recognitions.length > 0)) {
                            $.each(recognitions, function (i, item) {
                                $rcgList.append(makeRecognition(item));
                            });
                        } else {
                            if (init) {
                                $rcgList.addClass('rcg-list-empty');

                                $('<label/>', {
                                    class: 'rcg-list-empty-label',
                                    text: messageBundle['there.nothing']
                                }).appendTo($rcgList);
                            }
                        }

                        initTooltip();
                    }
                }).fail(function (jqXHR, textStatus, error) {
                    $rcgList.addClass('rcg-list-empty');

                    $('<label/>', {
                        class: 'rcg-list-empty-label',
                        text: jqXHR.responseText
                    }).appendTo($rcgList);
                }).always(function () {
                    loadingOnScroll = false;
                    $rcgWallWrapper.removeClass('rcg-list-loading');

                    if (init) {
                        $rcgWall.removeClass('rcg-wall-init');
                    }
                });
            }

            function loadComments(rcgId, page) {
                var $rcgLoadLink = $('.rcg-another-comments[rcg-id=' + rcgId + ']');

                $.ajax({
                    'url': webAppUrl + "/rest/v2/services/tsadv_RecognitionService/loadComments?recognitionId=" + rcgId + "&page=" + page,
                    'dataType': 'json',
                    'beforeSend': function (request) {
                        $rcgLoadLink.addClass('loading-another-comments');

                        request.setRequestHeader("Authorization", "Bearer " + accessToken);
                        request.setRequestHeader("Accept-Language", language);
                    }
                }).done(function (data, textStatus, jqXHR) {
                    if (data) {
                        var $commentList = $('.rcg-comment-list[rcg-id="' + rcgId + '"]');
                        $.each(data, function (i, item) {
                            $commentList.prepend(makeComment(rcgId, item, page));
                        });
                    }
                }).always(function () {
                    $rcgLoadLink.removeClass('loading-another-comments');
                });
            }
        }

        function isScrolledIntoView(element) {
            var docViewTop = $win.scrollTop();
            var docViewBottom = docViewTop + $win.height();

            var elemTop = $(element).offset().top;
            var elemBottom = elemTop + $(element).height();

            return ((elemBottom <= docViewBottom) && (elemTop >= docViewTop));
        }

        function refreshTabCounter(tabIndex) {
            if (tabIndex !== null)
                feedbackCount(tabIndex, function (count) {
                    var $spanCounter = $('.ul-li-div-counter span');
                    $spanCounter.text(count === 0 ? '' : count.toString());
                    $spanCounter.removeClass(count !== 0 ? 'span-count-no-element' : 'span-count')
                        .addClass(count === 0 ? 'span-count-no-element' : 'span-count');
                });
        }

        function createTab(name, handler, active) {
            return $('<li/>', {
                append: $('<a/>', {
                    href: '#',
                    class: active ? 'h-tab-active' : null,
                    text: name,
                    click: handler
                })
            });
        }

        function createTabWithCount(name, handler, active, tabIndex) {
            var li = $('<li/>', {
                append: $('<div/>', {
                    class: 'ul-li-div-counter',
                    style: 'display: flex',
                    append: $('<a/>', {
                        href: '#',
                        class: active ? 'h-tab-active' : null,
                        text: name,
                        click: handler
                    }).add('<span/>', {
                        name: 'span-count',
                        class: 'span-count-no-element',
                        text: ''
                    })
                })
            });

            refreshTabCounter(tabIndex);

            return li;
        }

        function resetLink(link) {
            $links.find('a').removeClass('h-tab-active');
            $(link).addClass('h-tab-active');
            $rcgWall.empty();
        }

        $win.unbind('scroll');
        $win.bind('scroll', function () {
            var y = $(this).scrollTop(), height = 75, $bannerList = $('.rcg-banner-list');
            if (pageName === 'main') {
                height += $('.rcg-block-w').height();
            } else {
                height += $('.rcg-pr-wrapper').height() - 25;
            }

            var diff = y + $('.rcg-content-wrapper').height(),
                wh = $('.rcg-wall-widget').height();

            if (diff < wh) {
                if (y >= height) {
                    $bannerList.css({
                        'position': 'absolute',
                        'top': (y - height) + 'px'
                    });
                } else {
                    $bannerList.css({
                        'position': 'relative',
                        'top': 'auto'
                    });
                }
            }

            if (!loadingOnScroll && currentPage < pagesCount) {
                var currY = $(this).scrollTop();
                var postHeight = $(this).height();
                var scrollPercent = (currY / (wh - postHeight)) * 100;
                if (scrollPercent > 85) {
                    loadingOnScroll = true;
                    loadingFunction.apply();
                }
            }
        });
    } else {

    }

    $('.rcg-number').forceNumericOnly();
    initTooltip();

    if (pageName === 'profile' && wallType === 0) {
        showTooltip('.rcg-pr-image-null');
    } else {
        var tps = $('.tippy-popper');
        if (tps) tps.remove();
    }
};