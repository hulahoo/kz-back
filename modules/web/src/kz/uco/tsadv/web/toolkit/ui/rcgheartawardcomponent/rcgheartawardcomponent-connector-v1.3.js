kz_uco_tsadv_web_toolkit_ui_rcgheartawardcomponent_RcgHeartAwardComponent = function () {
    var connector = this,
        element = connector.getElement(),
        state = connector.getState(),
        currentPage = 0,
        recordsCount = 0,
        pagesCount = 0,
        accessToken = state.authorizationToken,
        loadingData = false,
        webAppUrl = state.webAppUrl,
        tabId = null,
        loadingFunction = null,
        language = state.language,
        currentYear = state.currentYear,
        lastYear = state.lastYear,
        messageBundle = JSON.parse(state.messageBundle),
        aboutHaUrl = state.aboutHaUrl;

    var $win = $('.rcg-content-wrapper');

    connector.onStateChange = function () {
        //TODO:
    };

    connector.refreshActiveTab = function () {
        $win.trigger('refreshActiveTabEvent')
    };

    $win.unbind('scroll');
    $win.bind('scroll', function () {
        if (!loadingData && currentPage < pagesCount) {
            var currY = $(this).scrollTop();
            var postHeight = $(this).height();
            var scrollHeight = $('.rcg-heart-award-widget').height();
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

    function initPage() {
        if (tabId === null) tabId = 1;

        var $contentBody = $('<div/>', {
            class: 'rcg-sh-b rcg-ha-b'
        });

        initTemplate();

        function initTemplate() {
            var $tabsUl = $('<ul/>', {
                    class: 'rcg-sh-h-menu rcg-sh-h-menu-l',
                    'active-tab': tabId,
                    append: $('<li/>', {
                        text: messageBundle['winners'],
                        click: function () {
                            activateTab(this, 1);
                        }
                    }).add($('<li/>', {
                        text: messageBundle['my.nominee'],
                        click: function () {
                            activateTab(this, 2);
                        }
                    }))
                }),
                $headerRow = $('<div/>', {
                    class: 'row rcg-sh-h-row',
                    append: $('<div/>', {
                        class: 'col-lg-6',
                        append: $tabsUl
                    }).add($('<div/>', {
                        class: 'col-lg-6',
                        append: $('<ul/>', {
                            class: 'rcg-sh-h-menu rcg-sh-h-menu-r',
                            append: $('<li/>', {
                                append: $('<a/>', {
                                    href: aboutHaUrl ? aboutHaUrl : '#',
                                    target: "_blank",
                                    class: 'rcg-ha-about-ha-link rcg-btn rcg-white-btn rcg-btn-md',
                                    text: messageBundle['learn.about.ha'],
                                    prepend: $('<i/>', {
                                        class: 'fa fa-info-circle'
                                    })
                                }).add($('<a/>', {
                                    href: '#',
                                    class: 'rcg-ha-nominate-link rcg-btn rcg-blue-btn rcg-btn-md',
                                    text: messageBundle['nominate'],
                                    prepend: $('<i/>', {
                                        //class: 'fa fa-trophy'
                                    }),
                                    click: function () {
                                        connector.nominate(tabId);
                                    }
                                }))
                            })
                        })
                    }))
                });

            function activateTab(li, t) {
                $(li).parent().attr('active-tab', t);
                tabId = t;
                openTab()
            }

            openTab();

            $win.unbind('refreshActiveTabEvent');
            $win.bind('refreshActiveTabEvent', function () {
                if (tabId === null) tabId = 1;
                $tabsUl.attr('active-tab', tabId);
                openTab();
            });

            function openTab() {
                currentPage = recordsCount = pagesCount = 0;
                $contentBody.empty();

                if (tabId === 1) {
                    initWinners();
                } else {
                    initMyNominees();
                }
            }

            function loadYears(awarded, $select, $selectSumo, onChange) {
                $.ajax({
                    'url': webAppUrl + "/rest/v2/services/tsadv_RecognitionService/loadNomineeYears",
                    'dataType': 'json',
                    data: {
                        "awarded": awarded
                    },
                    'beforeSend': function (request) {
                        $selectSumo.disable();
                        $selectSumo.CaptionCont.addClass('ss-loading');

                        request.setRequestHeader("Authorization", "Bearer " + accessToken);
                        request.setRequestHeader("Accept-Language", language);
                    }
                }).done(function (data, textStatus, jqXHR) {
                    if (data) {
                        $.each(data, function (k, v) {
                            $select.append($('<option/>', {
                                value: v,
                                text: v,
                                title: v
                            }));
                        });
                        $select.val(currentYear);
                        $select.on('change', onChange);
                    }
                }).fail(function (jqXHR, textStatus, error) {
                    alert(jqXHR.responseText);
                }).always(function () {
                    $selectSumo.CaptionCont.removeClass('ss-loading');
                    $selectSumo.enable();
                    $selectSumo.reload();
                });
            }

            function loadOrganizations($select, $selectSumo, selector) {
                $.ajax({
                    'url': webAppUrl + "/rest/v2/services/tsadv_RecognitionService/loadOrganizations",
                    'dataType': 'json',
                    'beforeSend': function (request) {
                        $selectSumo.disable();
                        $selectSumo.CaptionCont.addClass('ss-loading');

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
                    alert(jqXHR.responseText);
                }).always(function () {
                    $selectSumo.CaptionCont.removeClass('ss-loading');
                    $selectSumo.enable();
                    $selectSumo.reload();
                    selector += '-wrap';
                    $select.parent().addClass(selector);

                    tippy('.' + selector + ' .options .opt', {
                        arrow: true,
                        animation: 'shift-away',
                        theme: 'light',
                        zIndex: 99999,
                        flip: false,
                        placement: 'top'
                    });
                });
            }

            function initAllNominees() {
                var amOrgGroupId = "-1", amYear = currentYear, amlastYear = lastYear, onChange = false;

                var $amYearSelect = $('<select/>', {
                        class: 'rcg-nominee-select-years'
                    }),
                    $amOrgSelect = $('<select/>', {
                        class: 'rcg-all-nominee-select-org',
                        append: $('<option/>', {
                            value: '-1',
                            text: messageBundle['all.organizations']
                        })
                    }),
                    $amContentHeader = $('<div/>', {
                        class: 'rcg-sh-b-h row',
                        append: $('<div/>', {
                            class: 'col-lg-6 rcg-reset-col-l',
                            append: $('<div/>', {
                                class: 'rcg-ha-nominee-title',
                                text: messageBundle['all.nominee.ha']
                            })
                        }).add($('<div/>', {
                            class: 'col-lg-6 rcg-align-r',
                            append: $amYearSelect.add($amOrgSelect)
                        }))
                    }),
                    $amTableBody = $('<tbody/>'),
                    $amTable = $('<table/>', {
                        class: 'rcg-all-nominee-table',
                        append: $('<thead/>', {
                            append: $('<tr/>', {
                                append: $('<td/>', {
                                    text: messageBundle['all.nominee.td.1']
                                }).add($('<td/>', {
                                    text: messageBundle['all.nominee.td.2']
                                })).add($('<td/>', {
                                    text: messageBundle['all.nominee.td.3']
                                })).add($('<td/>', {
                                    text: messageBundle['all.nominee.td.4']
                                }))
                            })
                        }).add($amTableBody)
                    }),
                    $amLoadMore = $('<div/>', {
                        class: 'rcg-sh-go-lm-w',
                        append: $('<a/>', {
                            class: 'rcg-sh-go-lm rcg-btn rcg-white-btn rcg-btn-sm',
                            href: '#',
                            text: messageBundle['show.more'],
                            click: function () {
                                loadingData = true;
                                loadNominees();
                            }
                        })
                    }),
                    $nomineeListWrapper = $('<div/>', {
                        class: 'rcg-sh-b-w',
                        append: $amContentHeader.add($amTable)
                    }),
                    $nomineeLoading = $('<div/>', {
                        class: 'rcg-sh-b-w-loading'
                    });

                $contentBody.append($nomineeListWrapper);

                initAmYears();
                initAmOrganizations();
                // loadNominees();
                loadNominees($amYearSelect);

                loadingFunction = function () {
                    loadNominees();
                };

                // function loadNominees() {
                function loadNominees($selectDate) {
                    var init = currentPage === 0;
                    currentPage++;

                    $.ajax({
                        'url': webAppUrl + "/rest/v2/services/tsadv_RecognitionService/loadAllNomineeWithDefault",
                        data: {
                            page: currentPage,
                            lastCount: recordsCount,
                            year: amYear,
                            organizationGroupId: amOrgGroupId,
                            lastYear: amlastYear,
                            onChange: onChange
                        },
                        'dataType': 'json',
                        'beforeSend': function (request) {
                            $nomineeListWrapper.append($nomineeLoading);

                            request.setRequestHeader("Authorization", "Bearer " + accessToken);
                            request.setRequestHeader("Accept-Language", language);

                            if (init) $amTableBody.empty();
                            $amLoadMore.hide();
                        }
                    }).done(function (data, textStatus, jqXHR) {
                        if (data) {
                            pagesCount = Number(data.pageInfo.pagesCount);
                            recordsCount = Number(data.pageInfo.totalRowsCount);

                            var nominees = data.nominees;
                            if ($selectDate && nominees.length) {
                                $selectDate.val(nominees[0].year);
                                $selectDate[0].sumo.reload();
                            }
                            if (recordsCount > 0 && (nominees && nominees.length > 0)) {
                                $.each(nominees, function (i, nominee) {
                                    $amTableBody.append(makeNominee(nominee));
                                });
                            } else {
                                if (init) {
                                    $amTableBody.append(emptyAmBlock(messageBundle['there.nothing']));
                                }
                            }
                        }
                    }).fail(function (jqXHR, textStatus, error) {
                        $amTableBody.append(emptyAmBlock(jqXHR.responseText));
                    }).always(function () {
                        loadingData = false;

                        $nomineeLoading.detach();

                        if (currentPage < pagesCount) {
                            $amLoadMore.show();
                        }
                    });

                    function makeNominee(nominee) {
                        return $('<tr/>', {
                            append: $('<td/>', {
                                append: $('<a/>', {
                                    href: '#',
                                    text: nominee.fullName,
                                    click: function () {
                                        connector.openProfilePage(nominee.pgId);
                                    }
                                }),
                                prepend: $('<img/>', {
                                    src: nominee.image
                                })
                            }).add($('<td/>', {
                                text: nominee.position
                            })).add($('<td/>', {
                                text: nominee.organization
                            })).add($('<td/>', {
                                text: nominee.year
                            }))
                        });
                    }

                    function emptyAmBlock(message) {
                        return $('<tr/>', {
                            append: $('<td/>', {
                                class: 'rcg-nominee-empty-tr',
                                colspan: 4,
                                text: message
                            })
                        })
                    }
                }

                function initAmYears() {
                    $amYearSelect.SumoSelect({search: true, searchText: messageBundle['search']});
                    loadYears(0, $amYearSelect, $amYearSelect[0].sumo, function () {
                        var selectYear = $amYearSelect.val();
                        if (selectYear !== amYear) {
                            currentPage = recordsCount = pagesCount = 0;
                            amYear = selectYear;
                            onChange = true;
                            loadNominees();
                        }
                    });
                }

                function initAmOrganizations() {
                    $amOrgSelect.SumoSelect({search: true, searchText: messageBundle['search']});
                    $amOrgSelect.on('change', function () {
                        var selectedOrg = $(this).val();
                        if (selectedOrg !== amOrgGroupId) {
                            currentPage = recordsCount = pagesCount = 0;
                            amOrgGroupId = selectedOrg;

                            loadNominees();
                        }
                    });
                    loadOrganizations($amOrgSelect, $amOrgSelect[0].sumo, 'rcg-all-nominee-select-org');
                }
            }

            function initWinners() {
                var organizationGroupId = "-1", year = currentYear;

                var $yearSelect = $('<select/>', {
                        class: 'rcg-nominee-select-years'
                    }),
                    $organizationSelect = $('<select/>', {
                        class: 'rcg-winner-select-org',
                        append: $('<option/>', {
                            value: '-1',
                            text: messageBundle['all.organizations']
                        })
                    }),
                    $contentHeader = $('<div/>', {
                        class: 'rcg-sh-b-h row',
                        append: $('<div/>', {
                            class: 'col-lg-6 rcg-reset-col-l',
                            append: $('<div/>', {
                                class: 'rcg-ha-nominee-title',
                                text: messageBundle['nominees.ha']
                            })
                        }).add($('<div/>', {
                            class: 'col-lg-6 rcg-align-r',
                            append: $yearSelect.add($organizationSelect)
                        }))
                    }),
                    $winnerList = $('<div/>', {
                        class: 'row rcg-winner-list'
                    }),
                    $winnerListWrapper = $('<div/>', {
                        class: 'rcg-sh-b-w',
                        append: $contentHeader.add($winnerList)
                    }),
                    $winnerLoading = $('<div/>', {
                        class: 'rcg-sh-b-w-loading'
                    }),
                    showAllText = messageBundle['show.more'],
                    slideUpText = messageBundle['hide'],
                    $showAllLink = $('<div/>', {
                        class: 'rcg-winner-show-more',
                        append: $('<a/>', {
                            href: '#',
                            'rcg-winner-vm': 'min',
                            text: showAllText,
                            append: $('<i/>', {
                                class: 'fa fa-chevron-down'
                            }),
                            click: function () {
                                var _self = $(this),
                                    vm = _self.attr('rcg-winner-vm');
                                if (vm === 'min') {
                                    $winnerList.find('.rcg-nominee-card-col:gt(7)').show();
                                    _self.attr('rcg-winner-vm', 'max');
                                    _self.text(slideUpText).append($('<i class="fa fa-chevron-up"/>'));
                                } else {
                                    $winnerList.find('.rcg-nominee-card-col:gt(7)').hide();
                                    _self.attr('rcg-winner-vm', 'min');
                                    _self.text(showAllText).append($('<i class="fa fa-chevron-down"/>'));
                                }
                            }
                        })
                    });

                $contentBody.append($winnerListWrapper);

                initYears();
                initOrganizations();
                loadWinners(true);

                function loadWinners(init) {
                    $.ajax({
                        'url': webAppUrl + "/rest/v2/services/tsadv_RecognitionService/loadTopNominee",
                        data: {
                            year: year,
                            organizationGroupId: organizationGroupId
                        },
                        'dataType': 'json',
                        'beforeSend': function (request) {
                            $winnerList.empty();
                            $showAllLink.detach();
                            $winnerListWrapper.append($winnerLoading).addClass('rcg-sh-b-w-d');

                            request.setRequestHeader("Authorization", "Bearer " + accessToken);
                            request.setRequestHeader("Accept-Language", language);
                        }
                    }).done(function (data, textStatus, jqXHR) {
                        if (data) {
                            var count = data.length;
                            if (count > 0) {
                                $.each(data, function (i, item) {
                                    $winnerList.append(makeWinner(item));
                                });
                                if (count > 8) {
                                    $winnerListWrapper.append($showAllLink);
                                }
                            } else {
                                $winnerList.append(emptyBlock(messageBundle['there.nothing']));
                            }
                        }
                    }).fail(function (jqXHR, textStatus, error) {
                        $winnerList.append(emptyBlock(jqXHR.responseText));
                    }).always(function () {
                        loadingData = false;

                        $winnerListWrapper.removeClass('rcg-sh-b-w-d');
                        $winnerLoading.detach();
                        $winnerList.find('.rcg-nominee-card-col:gt(7)').hide();

                        if (init) initAllNominees();
                    });

                    function makeWinner(winner) {
                        return $('<div/>', {
                            class: 'col-lg-3 col-md-4 col-sm-6 col-xs-12 rcg-reset-col-l rcg-nominee-card-col',
                            append: $('<div/>', {
                                class: 'rcg-nominee-card-w',
                                append: $('<div/>', {
                                    class: 'rcg-nominee-card',
                                    append: $('<div/>', {
                                        class: 'side',
                                        append: $('<div/>', {
                                            class: 'rcg-nominee-card-image-w',
                                            append: $('<img/>', {
                                                class: 'rcg-nominee-card-image',
                                                src: winner.image
                                            }).add($('<i/>'))
                                        }).add($('<div/>', {
                                            class: 'rcg-nominee-card-fn',
                                            text: winner.fullName
                                        })).add($('<div/>', {
                                            class: 'rcg-nominee-card-pos',
                                            text: winner.position
                                        })).add($('<div/>', {
                                            class: 'rcg-nominee-card-org',
                                            text: winner.organization
                                        })).add($('<div/>', {
                                            class: 'rcg-nominee-card-f',
                                            append: $('<div/>', {
                                                class: 'rcg-nominee-card-fw',
                                                text: messageBundle['winner']
                                            }).add($('<div/>', {
                                                class: 'rcg-nominee-card-fy',
                                                text: winner.year
                                            }))
                                        }))
                                    }).add($('<div/>', {
                                        class: 'back',
                                        append: $('<div/>', {
                                            class: 'rcg-nominee-card-desc',
                                            text: winner.description
                                        }).add($('<div/>', {
                                            class: 'rcg-nominee-card-desc-ra',
                                            text: messageBundle['read.story.full'],
                                            click: function () {
                                                connector.readStoryFull(JSON.stringify(winner));
                                            }
                                        }))
                                    }))
                                })
                            })
                        });
                    }

                    function emptyBlock(message) {
                        return $('<div/>', {
                            class: 'col-lg-12',
                            append: $('<div/>', {
                                class: 'rcg-team-empty',
                                text: message
                            })
                        });
                    }
                }

                function initYears() {
                    $yearSelect.SumoSelect({search: true, searchText: messageBundle['search']});
                    loadYears(1, $yearSelect, $yearSelect[0].sumo, function () {
                        var selectYear = $yearSelect.val();
                        if (selectYear !== year) {
                            year = selectYear;
                            loadWinners(false);
                        }
                    });
                }

                function initOrganizations() {
                    $organizationSelect.SumoSelect({search: true, searchText: messageBundle['search']});
                    $organizationSelect.on('change', function () {
                        var selectOrg = $(this).val();
                        if (selectOrg !== organizationGroupId) {
                            organizationGroupId = selectOrg;

                            loadWinners(false);
                        }
                    });

                    loadOrganizations($organizationSelect, $organizationSelect[0].sumo, 'rcg-winner-select-org');
                }
            }

            function initMyNominees() {
                var mnOrgGroupId = "-1", mnYear = -1;

                var $mnYearSelect = $('<select/>', {
                        class: 'rcg-nominee-select-years',
                        append: $('<option/>', {
                            value: '-1',
                            text: messageBundle['all.time']
                        }),
                        change: function () {
                            var selectYear = $(this).val();
                            if (selectYear !== mnYear) {
                                currentPage = recordsCount = pagesCount = 0;
                                mnYear = selectYear;
                                loadMnNominees();
                            }
                        }
                    }),
                    $mnOrgSelect = $('<select/>', {
                        class: 'rcg-my-nominee-select-org',
                        append: $('<option/>', {
                            value: '-1',
                            text: messageBundle['all.organizations']
                        })
                    }),
                    $mnContentHeader = $('<div/>', {
                        class: 'rcg-sh-b-h row',
                        append: $('<div/>', {
                            class: 'col-lg-6 rcg-reset-col-l',
                            append: $('<div/>', {
                                class: 'rcg-ha-nominee-title',
                                text: messageBundle['nominees']
                            })
                        }).add($('<div/>', {
                            class: 'col-lg-6 rcg-align-r',
                            append: $mnYearSelect.add($mnOrgSelect)
                        }))
                    }),
                    $mnTableBody = $('<tbody/>'),
                    $mnTable = $('<table/>', {
                        class: 'rcg-all-nominee-table',
                        append: $('<thead/>', {
                            append: $('<tr/>', {
                                append: $('<td/>', {
                                    text: messageBundle['all.nominee.td.1']
                                }).add($('<td/>', {
                                    text: messageBundle['all.nominee.td.2']
                                })).add($('<td/>', {
                                    text: messageBundle['all.nominee.td.3']
                                })).add($('<td/>', {
                                    text: messageBundle['all.nominee.td.4']
                                })).add($('<td/>'))
                            })
                        }).add($mnTableBody)
                    }),
                    $mnLoadMore = $('<div/>', {
                        class: 'rcg-sh-go-lm-w',
                        append: $('<a/>', {
                            class: 'rcg-sh-go-lm rcg-btn rcg-white-btn rcg-btn-sm',
                            href: '#',
                            text: messageBundle['load.more'],
                            click: function () {
                                loadingData = true;
                                loadNominees();
                            }
                        })
                    }),
                    $myNomineeListWrapper = $('<div/>', {
                        class: 'rcg-sh-b-w',
                        append: $mnContentHeader.add($mnTable)
                    }),
                    $myNomineeLoading = $('<div/>', {
                        class: 'rcg-sh-b-w-loading'
                    });


                initMnYears();
                initMnOrganizations();
                loadMnNominees();

                loadingFunction = function () {
                    loadMnNominees();
                };

                function loadMnNominees() {
                    var init = currentPage === 0;
                    currentPage++;

                    $.ajax({
                        'url': webAppUrl + "/rest/v2/services/tsadv_RecognitionService/loadMyNominees",
                        data: {
                            page: currentPage,
                            lastCount: recordsCount,
                            year: mnYear,
                            organizationGroupId: mnOrgGroupId
                        },
                        'dataType': 'json',
                        'beforeSend': function (request) {
                            $myNomineeListWrapper.append($myNomineeLoading);

                            request.setRequestHeader("Authorization", "Bearer " + accessToken);
                            request.setRequestHeader("Accept-Language", language);

                            if (init) $mnTableBody.empty();
                            $mnLoadMore.hide();
                        }
                    }).done(function (data, textStatus, jqXHR) {
                        if (data) {
                            pagesCount = Number(data.pageInfo.pagesCount);
                            recordsCount = Number(data.pageInfo.totalRowsCount);

                            var nominees = data.nominees;
                            if (recordsCount > 0 && (nominees && nominees.length > 0)) {
                                $.each(nominees, function (i, nominee) {
                                    $mnTableBody.append(makeMyNominee(nominee));
                                });
                            } else {
                                if (init) {
                                    $mnTableBody.append(emptyMnBlock(messageBundle['there.nothing']));
                                }
                            }
                        }
                    }).fail(function (jqXHR, textStatus, error) {
                        $mnTableBody.append(emptyMnBlock(jqXHR.responseText));
                    }).always(function () {
                        loadingData = false;

                        $myNomineeLoading.detach();
                        initTooltip();

                        if (currentPage < pagesCount) {
                            $mnLoadMore.show();
                        }
                    });

                    function makeMyNominee(nominee) {
                        return $('<tr/>', {
                            append: $('<td/>', {
                                append: $('<a/>', {
                                    href: '#',
                                    text: nominee.fullName,
                                    click: function () {
                                        connector.readPersonAward(nominee.personAwardId);
                                    }
                                }),
                                prepend: $('<img/>', {
                                    src: nominee.image
                                })
                            }).add($('<td/>', {
                                text: nominee.position
                            })).add($('<td/>', {
                                text: nominee.organization
                            })).add($('<td/>', {
                                text: nominee.year
                            })).add($('<td/>', {
                                append: $('<i/>', {
                                    class: 'rcg-nominee-edit-link fa fa-pencil rcg-tooltip',
                                    title: messageBundle['edit.2'],
                                    'data-tippy-flip': 'false',
                                    'data-tippy-theme': 'dark',
                                    click: function () {
                                        connector.editPersonAward(nominee.personAwardId);
                                    }
                                })
                            }))
                        });
                    }

                    function emptyMnBlock(message) {
                        return $('<tr/>', {
                            append: $('<td/>', {
                                class: 'rcg-nominee-empty-tr',
                                colspan: 4,
                                text: message
                            })
                        })
                    }
                }

                function initMnYears() {
                    $mnYearSelect.SumoSelect({search: true, searchText: messageBundle['search']});
                    var $mnYearSumo = $mnYearSelect[0].sumo;

                    $.ajax({
                        'url': webAppUrl + "/rest/v2/services/tsadv_RecognitionService/loadMyNomineeYears",
                        'dataType': 'json',
                        'beforeSend': function (request) {
                            $mnYearSumo.disable();
                            $mnYearSumo.CaptionCont.addClass('ss-loading');

                            request.setRequestHeader("Authorization", "Bearer " + accessToken);
                            request.setRequestHeader("Accept-Language", language);
                        }
                    }).done(function (data, textStatus, jqXHR) {
                        if (data) {
                            $.each(data, function (k, v) {
                                $mnYearSelect.append($('<option/>', {
                                    value: v,
                                    text: v,
                                    title: v
                                }));
                            });
                        }
                    }).fail(function (jqXHR, textStatus, error) {
                        alert(jqXHR.responseText);
                    }).always(function () {
                        $mnYearSumo.CaptionCont.removeClass('ss-loading');
                        $mnYearSumo.enable();
                        $mnYearSumo.reload();
                    });
                }

                function initMnOrganizations() {
                    $mnOrgSelect.SumoSelect({search: true, searchText: messageBundle['search']});
                    $mnOrgSelect.on('change', function () {
                        var selectedOrg = $(this).val();
                        if (selectedOrg !== mnOrgGroupId) {
                            currentPage = recordsCount = pagesCount = 0;
                            mnOrgGroupId = selectedOrg;

                            loadMnNominees();
                        }
                    });

                    loadOrganizations($mnOrgSelect, $mnOrgSelect[0].sumo, 'rcg-my-nominee-select-org');
                }

                $contentBody.append($myNomineeListWrapper);
            }

            $(element).append($headerRow).append($contentBody);
        }
    }

    var tps = $('.tippy-popper');
    if (tps) tps.remove();
};