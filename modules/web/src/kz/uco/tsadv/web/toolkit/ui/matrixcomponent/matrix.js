$(function () {

    var LIST_COUNTER = 0;

    var List = function ($matrixList, options) {
        this.$matrixList = $matrixList;
        this.$options = options;
        this.$globalOptions = $matrixList.$options;
        this.$items = {};

        this._init();
    };

    List.prototype = {
        $matrixList: null,
        $el: null,
        $elWrapper: null,
        $options: {},
        $items: {},
        $globalOptions: {},
        $ul: null,
        $header: null,
        $title: null,
        $form: null,
        $footer: null,
        $body: null,

        eventsSuppressed: false,

        _init: function () {
            $.blockUI({message: '<p><img src="demo/images/spinner.gif" /> Пожалуйста подождите, идет загрузка данных...</p>'});

            var me = this;
            me.suppressEvents();
            if (!me.$options.id) {
                me.$options.id = 'matrix-list-' + (LIST_COUNTER++);
            }

            var $wrapper = $('<div class="matrix-wrapper col-lg-4 col-md-4 col-sm-4 col-xs-12"></div>');
            var $div = $('<div id="' + me.$options.id + '" class="matrix-list"></div>').appendTo($wrapper);

            if (me.$options.defaultStyle) {
                $div.addClass(me.$options.defaultStyle);
            }
            me.$el = $div;
            me.$elWrapper = $wrapper;
            me.$header = me._createHeader();
            me._createTitle();
            me.$body = me._createBody();
            me.$ul = me._createList();
            if (me.$options.items) {
                me._createItems(me.$options.items);
            }
            me.$body.append(me.$ul, me.$form);
            if (me.$globalOptions.sortable) {
                me._enableSorting();
            }
            me.resumeEvents();

            $('.matrix-item').tooltip();

            $.unblockUI();
        },

        suppressEvents: function () {
            this.eventsSuppressed = true;
            return this;
        },

        resumeEvents: function () {
            this.eventsSuppressed = false;
            return this;
        },

        _processItemData: function (item) {
            var me = this;
            return $.extend({}, me.$globalOptions.itemOptions, item);
        },

        _createHeader: function () {
            var me = this;
            var $header = $('<div>', {
                'class': 'matrix-list-header'
            });
            me.$el.append($header);
            return $header;
        },

        _createTitle: function () {
            var me = this;
            if (me.$options.title !== null) {
                $('<div>', {
                    'class': 'matrix-list-title',
                    html: me.$options.title
                }).appendTo(me.$header);
            }

            $('<div>', {
                'class': 'matrix-list-info',
                html: me.$options.info
            }).appendTo(me.$header);
        },

        _createBody: function () {
            var me = this;

            var $body = $('<div>', {
                'class': 'matrix-list-body'
            });

            if (LIST_COUNTER === 1 || LIST_COUNTER === 4 || LIST_COUNTER === 7) {
                var $vText = $('<div>', {
                    'class': 'v-text',
                    html: me.$options.vText
                });

                $body.append($vText);
                $body.addClass('has-v-text')
            }

            me.$el.append($body);
            return $body;
        },

        _createList: function () {
            var me = this;
            var $list = $('<ul>', {
                'class': 'matrix-items clearfix',
                'id': me.$options.listId
            });
            me.$el.append($list);
            return $list;
        },

        _createItems: function (items) {
            if (items != null && items.length != 0) {
                var me = this;
                for (var i = 0; i < items.length; i++) {
                    me._addItem(items[i]);
                }
            }
        },

        _addItem: function (item) {
            var me = this;
            if (!item.id) {
                item.id = me.$matrixList.getNextId();
            }
            if (me._triggerEvent('beforeItemAdd', [me, item]) !== false) {
                item = me._processItemData(item);
                me._addItemToList(item);
            }
        },

        _enableSorting: function () {
            var me = this;
            me.$el.find('.matrix-items').sortable({
                connectWith: '.matrix-list .matrix-items',
                items: '.matrix-item',
                handle: '.drag-handler',
                cursor: 'move',
                placeholder: 'matrix-item-placeholder',
                forcePlaceholderSize: true,
                opacity: 0.6,
                revert: 70,
                update: function (event, ui) {
                    me._triggerEvent('afterItemReorder', [me, ui.item]);
                },
                receive: function (event, ui) {
                    var data = {
                        id: ui.item.data('id'),
                        from: ui.sender.attr("id"),
                        to: this.id
                    };

                    me._triggerEvent('afterItemReceive', [me, JSON.stringify(data)]);
                }
            });
        },

        _addItemToList: function (item) {
            var me = this;
            var $li = $('<li>', {
                'data-id': item.id,
                'class': 'matrix-item',
                'title': item.title
            });

            $li.append($('<img>', {
                'class': 'matrix-item-image drag-handler',
                'src': item.url
            }));

            $li.data('matrixListItem', item);
            me.$ul.append($li);
            me.$items[item.id] = item;
            me._triggerEvent('afterItemAdd', [me, item]);

            return $li;
        },

        _triggerEvent: function (type, data) {
            var me = this;
            if (me.eventsSuppressed) {
                return;
            }
            if (me.$options[type] && typeof me.$options[type] === 'function') {
                return me.$options[type].apply(me, data);
            } else {
                return me.$el.trigger(type, data);
            }
        },

        _sendAjax: function (url, params) {
            var me = this;
            return $.ajax(url, me._beforeAjaxSent(params))
        },

        _beforeAjaxSent: function (params) {
            var me = this;
            var eventParams = me._triggerEvent('beforeAjaxSent', [me, params]);
            return $.extend({}, params, eventParams || {});
        }
    };
//||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||

    var MatrixList = function ($el, options) {
        LIST_COUNTER = 0;
        this.$el = $el;
        this.init(options);
    };

    MatrixList.prototype = {
        $el: null,
        $lists: [],
        $options: {},
        _nextId: 1,

        eventsSuppressed: false,

        init: function (options) {
            var me = this;
            me.suppressEvents();

            me.$options = this._processInput(options);
            me.$el.addClass('matrix row');
            if (me.$options.onSingleLine) {
                me.$el.addClass('single-line');
            }

            me._createLists();
            me._triggerEvent('init', [me]);
            me.resumeEvents();

            var sin = Number($('.v-slot-matrix-vbox').height() / 3);

            $('.matrix-list').each(function (k, v) {
                var hHead = $(this).find('.matrix-list-header').height();

                $(this).find('.matrix-list-body').css('height', sin - hHead - 25)
            });
        },

        _processInput: function (options) {
            options = $.extend({}, $.fn.matrixList.DEFAULT_OPTIONS, options);
            if (options.actions.load) {
                $.ajax(options.actions.load, {
                    async: false,
                    beforeSend: function (request) {
                        request.setRequestHeader("Authorization", "Bearer " + options.authorizationToken); //+ for rest api security
                    }
                }).done(function (res) {
                    var json = JSON.parse(res);
                    options.lists = json.lists;
                });
            }
            return options;
        },

        _createLists: function () {
            var me = this;
            for (var i = 0; i < me.$options.lists.length; i++) {
                me.addList(me.$options.lists[i]);
            }
            return me;
        },
        destroy: function () {
            var me = this;
            if (me._triggerEvent('beforeDestroy', [me]) !== false) {
                for (var i = 0; i < me.$lists.length; i++) {
                    for (var j = 0; j < me.$lists[i].$items.length; j++) {
                        me.$lists[i].$items[j].remove();
                    }
                }

                if (me.$options.sortable) {
                    me.$el.find('.matrix-items').sortable("destroy");
                }
                me.$el.removeClass('matrix');
                me.$el.removeData('matrixList');

                me.$el.empty();
                me._triggerEvent('afterDestroy', [me]);
            }

            return me;
        },

        addList: function (list) {
            var me = this;
            if (!(list instanceof List)) {
                list = new List(me, me._processListOptions(list));
            }
            if (me._triggerEvent('beforeListAdd', [me, list]) !== false) {
                me.$lists.push(list);
                me.$el.append(list.$elWrapper);
                list.$el.data('matrixList', list);
                me._triggerEvent('afterListAdd', [me, list]);
            }
            return list;
        },

        getNextId: function () {
            return this._nextId++;
        },

        _processListOptions: function (listOptions) {
            var me = this;
            listOptions = $.extend({}, me.$options.listsOptions, listOptions);

            for (var i in me.$options) {
                if (me.$options.hasOwnProperty(i) && listOptions[i] === undefined) {
                    listOptions[i] = me.$options[i];
                }
            }
            return listOptions;
        },

        suppressEvents: function () {
            this.eventsSuppressed = true;
            return this;
        },

        resumeEvents: function () {
            this.eventsSuppressed = false;
            return this;
        },

        _triggerEvent: function (type, data) {
            var me = this;
            if (me.eventsSuppressed) {
                return;
            }
            if (me.$options[type] && typeof me.$options[type] === 'function') {
                return me.$options[type].apply(me, data);
            } else {
                return me.$el.trigger(type, data);
            }
        }
    };

    $.fn.matrixList = function (option) {
        var args = arguments;
        var ret;
        return this.each(function () {
            var $this = $(this);
            var data = $this.data('matrixList');
            var options = typeof option === 'object' && option;

            if (!data) {
                $this.data('matrixList', (data = new MatrixList($this, options)));
            }
            if (typeof option === 'string') {
                args = Array.prototype.slice.call(args, 1);
                ret = data[option].apply(data, args);
            }
        });
    };
    $.fn.matrixList.DEFAULT_OPTIONS = {
        // Default options for all lists
        listsOptions: {
            id: false,
            title: '',
            items: []
        },
        // Default options for all todo items
        itemOptions: {
            id: false,
            title: '',
            url: ''
        },

        lists: [],
        // Urls to communicate to backend for todos
        actions: {
            'load': '',
            'update': '',
            'insert': '',
            'delete': ''
        },
        // Whether to make lists and todos sortable
        listId: '',
        sortable: true,
        //List style
        defaultStyle: 'matrix-list-1',
        // Whether to show lists on single line or not
        onSingleLine: false,

        // Events
        init: null,
        beforeListAdd: null,
        afterListAdd: null,
        beforeItemAdd: null,
        afterItemAdd: null,
        beforeItemUpdate: null,
        afterListReorder: null,
        afterItemReorder: null,
        beforeAjaxSent: null,
        afterItemReceive: null
    };
});