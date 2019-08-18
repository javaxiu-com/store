<template>

    <div>
        <!--新增品牌+搜索-->
        <v-layout row wrap>
        <v-flex xs12 sm6>
        <v-btn color="info">新增品牌</v-btn>
        </v-flex>
        <v-flex xs12 sm6>
        <!--双向绑定,监控回车搜索-->
        <v-text-field
                label="搜索"
                v-model = "search"
                append-icon="search"
                @keyup.enter="loadData"
        ></v-text-field>
        </v-flex>
        </v-layout>
        <!--表格-->
        <v-data-table
                :headers="headers"
                :items="brands"
                :pagination.sync="pagination"
                :total-items="totalBrands"
                :loading="loading"
                class="elevation-1"
        >
            <template v-slot:items="props">
                <td class="text-xs-center"> {{ props.item.id }}</td>
                <td class="text-xs-center">{{ props.item.name }}</td>
                <td class="text-xs-center"><img :src="props.item.image"></td>
                <td class="text-xs-center">{{ props.item.letter }}</td>
                <td class="text-xs-center"></td>
            </template>
        </v-data-table>
    </div>
</template>

<script>
    export default {
        data () {
            return {
                totalBrands: 0,
                brands: [],
                loading: false,  //进度条
                pagination: {},
                search:'',
                headers: [
                    {
                        text: 'id', align: 'center', sortable: false, value: 'id'
                    },
                    { text: '名称',  align: 'center',value: 'name' },
                    { text: 'logo',  align: 'center',value: 'image' },
                    { text: '首字母',  align: 'center',value: 'letter' },
                    { text: '操作', align: 'center' }
                ]
            }
        },
        watch:{
            pagination:{ //分页点击事件监控
                deep:true,
                handler(){
                    this.loadData();
                }
            }
        },
        mounted () {// 渲染后执行
            // 查询数据
            this.loadData();
        },
        methods: {
            loadData () {
                //请求服务端
                //then(function(resp){
                // })
                this.loading = true;  //进度条
                this.$http.get('/item/brand/page',{params:{
                    key:this.search, // 搜索条件
                    page:this.pagination.page, // 当前页
                    rows:this.pagination.rowsPerPage, // 每页大小
                    sortBy:this.pagination.sortBy, // 排序字段
                    desc:this.pagination.descending // 是否降序
                }}).then(resp =>{
                    this.brands = resp.data.items;
                    this.totalBrands = resp.data.total;
                })
                this.loading = false;  //进度条
            },
            getDesserts () {
                return [
                    {
                        id:1,
                        name: 'Frozen Yogurt',
                        image: 'werwerwe',
                        letter: 'A'
                    }
                ]
            }
        }
    }
</script>