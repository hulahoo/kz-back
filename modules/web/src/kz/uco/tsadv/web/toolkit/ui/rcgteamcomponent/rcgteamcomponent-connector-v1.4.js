kz_uco_tsadv_web_toolkit_ui_rcgteamcomponent_RcgTeamComponent = function () {
    var connector = this,
        element = connector.getElement(),
        state = connector.getState(),
        currentPage = 0,
        profilesCount = 0,
        pagesCount = 0,
        accessToken = state.authorizationToken,
        loadingProfiles = false,
        viewType = 1,
        webAppUrl = state.webAppUrl,
        language = state.language,
        messageBundle = JSON.parse(state.messageBundle);

    var $rcgTeamWrapper = $('<div/>', {
        class: 'rcg-team-wrapper'
    }).appendTo($(element));

    var $rcgTeamLoading = $('<div/>', {
        class: 'rcg-team-loading'
    });

    var $rcgTeamList;

    var $win = $('.rcg-content-wrapper');

    $win.unbind('scroll');
    $win.bind('scroll', function () {
        if (!loadingProfiles && currentPage < pagesCount) {
            var currY = $(this).scrollTop();
            var postHeight = $(this).height();
            var scrollHeight = $('.rcg-page-content').height();
            var scrollPercent = (currY / (scrollHeight - postHeight)) * 100;
            if (scrollPercent > 85) {
                loadingProfiles = true;
                loadProfiles();
            }
        }
    });

    loadProfiles();

    function loadProfiles() {
        var init = currentPage === 0;
        currentPage++;

        if (init) {
            var $rcgTeamHeadTr = $('<tr/>', {
                append: $('<td/>', {
                    text: messageBundle['column.name']
                })
            });

            var $rcgTeamHeadWrapper = $('<div/>', {
                class: 'rcg-team-head-w'
            }).appendTo($rcgTeamWrapper);

            var $rcgTeamHead = $('<table/>', {
                class: 'rcg-team-head-table',
                append: $('<thead/>', {
                    append: $rcgTeamHeadTr
                })
            });

            if (viewType === 1) {
                $rcgTeamHeadWrapper.addClass('rcg-team-vtb');

                $rcgTeamList = $('<div/>', {
                    class: 'row'
                }).appendTo($rcgTeamWrapper);
            } else {
                $rcgTeamHeadTr.append($('<td/>', {
                    text: 'Thank You'
                })).append($('<td/>', {
                    text: 'Good Job'
                })).append($('<td/>', {
                    text: 'Outstanding'
                }));

                $rcgTeamList = $('<tbody/>')
                    .appendTo($rcgTeamHead);
            }

            $rcgTeamHeadWrapper.append($rcgTeamHead);

            var $lastTd = $('<td>', {
                append: $('<i/>', {
                    class: 'fa fa-th-large ' + (viewType === 1 ? 'rcg-team-view-active' : ''),
                    click: function () {
                        if (viewType !== 1) {
                            changeViewType($lastTd, this, 1);
                        }
                    }
                }).add($('<i/>', {
                    class: 'fa fa-th-list ' + (viewType === 2 ? 'rcg-team-view-active' : ''),
                    click: function () {
                        if (viewType !== 2) {
                            changeViewType($lastTd, this, 2);
                        }
                    }
                }))
            }).appendTo($rcgTeamHeadTr);

            function changeViewType(parent, el, type) {
                $(parent).find('i').removeClass('rcg-team-view-active');
                $(el).addClass('rcg-team-view-active');

                $rcgTeamWrapper.empty();
                viewType = type;
                currentPage = 0;
                loadProfiles();
            }
        }

        $.ajax({
            'url': webAppUrl + "/rest/v2/services/tsadv_RecognitionService/loadProfiles",
            data: {
                page: currentPage,
                lastCount: profilesCount
            },
            'dataType': 'json',
            'beforeSend': function (request) {
                $rcgTeamWrapper.append($rcgTeamLoading).addClass('rcg-team-wrapper-d');

                request.setRequestHeader("Authorization", "Bearer " + accessToken);
                request.setRequestHeader("Accept-Language", language);
            }
        }).done(function (data, textStatus, jqXHR) {
            if (data) {
                pagesCount = Number(data.pageInfo.pagesCount);
                profilesCount = Number(data.pageInfo.totalRowsCount);

                var profiles = data.profiles;
                if (profilesCount > 0 && (profiles && profiles.length > 0)) {
                    $.each(profiles, function (i, profile) {
                        $rcgTeamList.append(makeProfile(profile));
                    });
                } else {
                    if (init) {
                        $rcgTeamList.append(emptyBlock(messageBundle['there.nothing']));
                    }
                }

                initTooltip();
            }
        }).fail(function (jqXHR, textStatus, error) {
            $rcgTeamList.append(emptyBlock(jqXHR.responseText));
        }).always(function () {
            loadingProfiles = false;

            $rcgTeamWrapper.removeClass('rcg-team-wrapper-d');
            $rcgTeamLoading.detach();
        });
    }

    function emptyBlock(message) {
        var $block;
        if (viewType === 1) {
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

    function makeProfile(profile) {
        var rcgTypes = profile.recognitionTypes;

        var $actionsMenu = $('<div/>', {
            class: 'rcg-tpr-actions-menu',
            append: $('<a/>', {
                href: '#',
                text: messageBundle['send.rcg'],
                prepend: $('<i/>', {
                    class: 'fa fa-thumbs-up'
                }),
                click: function (e) {
                    e.stopImmediatePropagation();
                    connector.giveThanks(profile.pgId);
                }
            }).add($('<a/>', {
                href: '#',
                text: messageBundle['show.buy.history'],
                prepend: $('<i/>', {
                    class: 'fa fa-clock-o'
                }),
                click: function (e) {
                    e.stopImmediatePropagation();
                }
            })).add($('<a/>', {
                href: '#',
                text: messageBundle['team.analytics'],
                prepend: $('<i/>', {
                    class: 'fa fa-bar-chart'
                }),
                click: function (e) {
                    e.stopImmediatePropagation();
                    connector.showAnalytics(profile.pgId);
                }
            })).add($('<a/>', {
                href: '#',
                text: messageBundle['rcg.feedback.send.btn'],
                prepend: $('<i/>', {
                    class: 'fa fa-rss'
                }),
                click: function (e) {
                    e.stopImmediatePropagation();
                    connector.requestFeedback(profile.pgId);
                }
            }))
        });
        $actionsMenu.on("mouseleave", function () {
            $(this).hide();
        });
        $actionsMenu.hide();

        var $rcgActions = $('<div/>', {
            class: 'rcg-tpr-actions',
            append: $('<a/>', {
                text: '...',
                click: function (e) {
                    e.stopImmediatePropagation();
                    $actionsMenu.show();
                }
            }).add($actionsMenu)
        });

        var heartAward = profile.heartAward;

        if (viewType === 1) {
            var $profileCol = $('<div/>', {
                class: 'col-lg-4 col-md-6 col-sm-12 col-xs-12 rcg-tpr-col'
            });

            var $profileImageWrap = $('<div/>', {
                class: 'rcg-tpr-image-w',
                append: $('<img/>', {
                    class: 'rcg-tpr-image',
                    src: profile.image
                })
            });

            if (heartAward) {
                $profileImageWrap.addClass('rcg-tpr-heart-award rcg-tooltip');
                $profileImageWrap.attr('title', heartAward);
                $profileImageWrap.append($('<i/>', {
                    class: 'rcg-tpr-heart-award-icon'
                }));
            }

            var $profileWrapper = $('<div/>', {
                class: 'rcg-tpr-wrapper',
                click: function () {
                    connector.openProfilePage(profile.pgId);
                },
                append: $profileImageWrap.add($('<div/>', {
                    class: 'rcg-tpr-fio',
                    text: profile.fullName
                })).add($('<div/>', {
                    class: 'rcg-tpr-org',
                    text: profile.organization
                })).add($rcgActions)
            }).appendTo($profileCol);

            var $rcgTypesWrapper = $('<div/>', {
                class: 'rcg-tpr-thanks-wrap'
            }).appendTo($profileWrapper);

            if (rcgTypes) {
                $.each(rcgTypes, function (k, type) {
                    $rcgTypesWrapper.append($('<div/>', {
                        class: 'rcg-tpr-thanks-item',
                        append: $('<div/>', {
                            class: 'rcg-tpr-thanks-count',
                            text: type.count
                        }).add($('<div/>', {
                            class: 'rcg-tpr-thanks-type',
                            text: type.name
                        }))
                    }));
                });
            }
            return $profileCol;
        } else {
            var $tr = $('<tr/>', {
                append: $('<td/>', {
                    append: $('<img/>', {
                        src: profile.image
                    }).add($('<a/>', {
                        href: '#',
                        text: profile.fullName,
                        click: function () {
                            connector.openProfilePage(profile.pgId);
                        }
                    })).add(heartAward ? $('<span/>', {
                        class: 'rcg-tpr-table-ha',
                        append: $('<i/>', {
                            class: 'rcg-tooltip',
                            title: heartAward
                        })
                    }) : null)
                })
            });

            if (rcgTypes) {
                $.each(rcgTypes, function (k, type) {
                    $tr.append($('<td/>', {
                        text: type.count
                    }));
                });
            }

            $tr.append($('<td/>', {
                append: $rcgActions
            }));
            return $tr;
        }
    }

    var tps = $('.tippy-popper');
    if (tps) tps.remove();
};