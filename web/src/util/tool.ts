export class Tool {
    /**
     * 空校验 null或""都返回true
     */
    public static isEmpty(obj: any) {
        if ((typeof obj === 'string')) {
            return !obj || obj.replace(/\s+/g, "") === ""
        } else {
            return (!obj || JSON.stringify(obj) === "{}" || obj.length === 0);
        }
    }

    /**
     * 非空校验
     */
    public static isNotEmpty(obj: any) {
        return !this.isEmpty(obj);
    }

    /**
     * 对象复制
     */
    public static copy(obj: any) {
        if (Tool.isNotEmpty(obj)) {
            return JSON.parse(JSON.stringify(obj));
        }
    }

    /**
     * 使用递归将数组转为树形结构
     */
    public static array2Tree (array: any, parentId: number) {
        if (Tool.isEmpty(array)) {
            return [];
        }

        const result = [];
        for (let i = 0; i < array.length; i++) {
            if (Number(array[i].parentId) === Number(parentId)) {
                result.push(array[i]);

                // 递归查看当前节点对应的子节点
                const children = Tool.array2Tree(array, array[i].id);
                if (Tool.isNotEmpty(children)) {
                    array[i].children = children;
                }
            }
        }
        return result;
    }
}