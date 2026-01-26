/*
 * 时间选择框快捷选择
 *
 * @Author:    连接器-主任：卓大
 * @Date:      2022-09-06 20:49:28
 * @Wechat:    zhuda1024
 * @Email:     lab1024@163.com
 * @Copyright  连接器 （ https://www.zhaogang.com ），Since 2012
 */

import dayjs from 'dayjs';
import {ref} from 'vue';

export const defaultTimeRanges = ref([{label: '今日', value: [dayjs(), dayjs()]}, {
    label: '昨日',
    value: [dayjs().subtract(1, 'days'), dayjs().subtract(1, 'days')]
}, {label: '本月', value: [dayjs().startOf('month'), dayjs().endOf('month')]}, {
    label: '上个月',
    value: [dayjs().subtract(1, 'months').startOf('month'), dayjs().subtract(1, 'months').endOf('month')]
}, {
    label: '下个月',
    value: [dayjs().subtract(-1, 'months').startOf('month'), dayjs().subtract(-1, 'months').endOf('month')]
},

    {label: '本年度', value: [dayjs().startOf('year'), dayjs().endOf('year')]}, {
        label: '上年度',
        value: [dayjs().subtract(1, 'years').startOf('year'), dayjs().subtract(1, 'years').endOf('year')]
    }]);

