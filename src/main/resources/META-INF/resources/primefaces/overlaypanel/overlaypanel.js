/**
 * PrimeFaces OverlayPanel Widget
 */
PrimeFaces.widget.OverlayPanel = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.content = this.jq.children('div.ui-overlaypanel-content')
        this.targetId = PrimeFaces.escapeClientId(this.cfg.target);
        this.target = $(this.targetId);
    
        //configuration
        this.cfg.my = this.cfg.my||'left top';
        this.cfg.at = this.cfg.at||'left bottom';
        this.cfg.showEvent = this.cfg.showEvent||'click.ui-overlaypanel';
        this.cfg.hideEvent = this.cfg.hideEvent||'click.ui-overlaypanel';
        this.cfg.dismissable = (this.cfg.dismissable === false) ? false : true;
        
        if(this.cfg.showCloseIcon) {
            this.closerIcon = $('<a href="#" class="ui-overlaypanel-close ui-state-default" href="#"><span class="ui-icon ui-icon-closethick"></span></a>').appendTo(this.jq);
        }

        this.bindEvents();

        if(this.cfg.appendToBody) {
            this.jq.appendTo(document.body);
        }

        //dialog support
        this.setupDialogSupport();
    },
    
    bindEvents: function() {
        var $this = this;

        //mark target and descandants of target as a trigger for a primefaces overlay
        this.target.data('primefaces-overlay-target', this.id).find('*').data('primefaces-overlay-target', this.id);

        //show and hide events for target
        if(this.cfg.showEvent === this.cfg.hideEvent) {
            var event = this.cfg.showEvent;
            
            $(document).off(event, this.targetId).on(event, this.targetId, this, function(e) {
                e.data.toggle();
            });
        }
        else {
            var showEvent = this.cfg.showEvent + '.ui-overlaypanel',
            hideEvent = this.cfg.hideEvent + '.ui-overlaypanel';
            
            $(document).off(showEvent + ' ' + hideEvent, this.targetId).on(showEvent, this.targetId, this, function(e) {
                if(!e.data.isVisible()) {
                    e.data.show();
                }
            })
            .on(hideEvent, this.targetId, this, function(e) {
                if(e.data.isVisible()) {
                    e.data.hide();
                }
            });
        }
        
        //enter key support for mousedown event
        this.bindKeyEvents();
        
        if(this.cfg.showCloseIcon) {
            this.closerIcon.on('mouseover.ui-overlaypanel', function() {
                $(this).addClass('ui-state-hover');
            })
            .on('mouseout.ui-overlaypanel', function() {
                $(this).removeClass('ui-state-hover');
            })
            .on('click.ui-overlaypanel', function(e) {
                $this.hide();
                e.preventDefault();
            });
        }

        //hide overlay when mousedown is at outside of overlay
        if(this.cfg.dismissable) {
           $(document.body).bind('mousedown.ui-overlaypanel', function (e) {
                if($this.jq.hasClass('ui-overlay-hidden')) {
                    return;
                }

                //do nothing on target mousedown
                var target = $(e.target);
                if($this.target.is(target)||$this.target.has(target).length > 0) {
                    return;
                }

                //hide overlay if mousedown is on outside
                var offset = $this.jq.offset();
                if(e.pageX < offset.left ||
                    e.pageX > offset.left + $this.jq.outerWidth() ||
                    e.pageY < offset.top ||
                    e.pageY > offset.top + $this.jq.outerHeight()) {

                    $this.hide();
                }
            }); 
        }

        //Hide overlay on resize
        var resizeNS = 'resize.' + this.id;
        $(window).unbind(resizeNS).bind(resizeNS, function() {
            if($this.jq.hasClass('ui-overlay-visible')) {
                $this.align();
            }
        });
    },
    
    bindKeyEvents: function() {
        $(document).off('keydown.ui-overlaypanel keyup.ui-overlaypanel', this.targetId).on('keydown.ui-overlaypanel', this.targetId, this, function(e) {
            var keyCode = $.ui.keyCode, key = e.which;
            
            if(key === keyCode.ENTER||key === keyCode.NUMPAD_ENTER) {
                e.preventDefault();
            }
        })
        .on('keyup.ui-overlaypanel', this.targetId, this, function(e) {
            var keyCode = $.ui.keyCode, key = e.which;
            
            if(key === keyCode.ENTER||key === keyCode.NUMPAD_ENTER) {
                e.data.toggle();
                e.preventDefault();
            }
        });
    },
    
    toggle: function() {
        if(!this.isVisible())
            this.show();
        else
            this.hide();
    },
    
    show: function() {
        if(!this.loaded && this.cfg.dynamic)
            this.loadContents();
        else
            this._show();
    },
    
    _show: function() {
        var $this = this;

        this.align();

        //replace visibility hidden with display none for effect support, toggle marker class
        this.jq.removeClass('ui-overlay-hidden').addClass('ui-overlay-visible').css({
            'display':'none'
            ,'visibility':'visible'
        });

        if(this.cfg.showEffect) {
            this.jq.show(this.cfg.showEffect, {}, 200, function() {
                $this.postShow();
            });
        }
        else {
            this.jq.show();
            this.postShow();
        }
    },
    
    align: function() {
        var fixedPosition = this.jq.css('position') == 'fixed',
        win = $(window),
        positionOffset = fixedPosition ? '-' + win.scrollLeft() + ' -' + win.scrollTop() : null;

        this.jq.css({'left':'', 'top':'', 'z-index': ++PrimeFaces.zindex})
                .position({
                    my: this.cfg.my
                    ,at: this.cfg.at
                    ,of: document.getElementById(this.cfg.target)
                    ,offset: positionOffset
                });
    },
    
    hide: function() {
        var $this = this;

        if(this.cfg.hideEffect) {
            this.jq.hide(this.cfg.hideEffect, {}, 200, function() {
                $this.postHide();
            });
        }
        else {
            this.jq.hide();
            this.postHide();
        }
    },
    
    postShow: function() {
        if(this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }
        
        this.applyFocus();
    },
    
    postHide: function() {
        //replace display block with visibility hidden for hidden container support, toggle marker class
        this.jq.removeClass('ui-overlay-visible').addClass('ui-overlay-hidden').css({
            'display':'block'
            ,'visibility':'hidden'
        });
        
        if(this.cfg.onHide) {
            this.cfg.onHide.call(this);
        }

        
    },
    
    setupDialogSupport: function() {
        var dialog = this.target.parents('.ui-dialog:first');

        if(dialog.length == 1) {
            //set position as fixed to scroll with dialog
            this.jq.css('position', 'fixed');

            //append to body if not already appended by user choice
            if(!this.cfg.appendToBody) {
                this.jq.appendTo(document.body);
            }
        }
    },
    
    loadContents: function() {
        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            params: [
                {name: this.id + '_contentLoad', value: true}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.content.html(content);
                            this.loaded = true;
                        }
                    });

                return true;
            },
            oncomplete: function() {
                $this._show();
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    },
    
    isVisible: function() {
        return this.jq.hasClass('ui-overlay-visible');
    },
    
    applyFocus: function() {
        this.jq.find(':not(:submit):not(:button):input:visible:enabled:first').focus();
    }

});