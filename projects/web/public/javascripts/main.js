$(function () {
    $('.navbar-toggle').click(function () {
        $('.navbar-nav').toggleClass('slide-in');
        $('.side-body').toggleClass('body-slide-in');
    });

    // Bind all datetimepicker elements if they exist.
    if ($('[class^=datetimepicker-]').length > 0) {
        $('.datetimepicker-date').datetimepicker({
            locale : SharedLibrary.getLang()
            ,format : 'DD/MM/YYYY'
            ,useCurrent : true
            ,showTodayButton: false
            ,showClear: true
            ,tooltips: {
                today: datetimepickerTooltipsMap['today'],
                clear: datetimepickerTooltipsMap['clear'],
                close: datetimepickerTooltipsMap['close'],
                selectMonth: datetimepickerTooltipsMap['selectMonth'],
                prevMonth: datetimepickerTooltipsMap['prevMonth'],
                nextMonth: datetimepickerTooltipsMap['nextMonth'],
                selectYear: datetimepickerTooltipsMap['selectYear'],
                prevYear: datetimepickerTooltipsMap['prevYear'],
                nextYear: datetimepickerTooltipsMap['nextYear'],
                selectDecade: datetimepickerTooltipsMap['selectDecade'],
                prevDecade: datetimepickerTooltipsMap['prevDecade'],
                nextDecade: datetimepickerTooltipsMap['nextDecade'],
                prevCentury: datetimepickerTooltipsMap['prevCentury'],
                nextCentury: datetimepickerTooltipsMap['nextCentury']
            }
        });
    }
});