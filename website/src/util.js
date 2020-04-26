import get from 'lodash/get';

export function kotlinProxy(kotlinInstance, goDeep = true, autoProxyMethod = true) {
    if (kotlinInstance === undefined || kotlinInstance === null) {
        return kotlinInstance
    } else if (typeof kotlinInstance === 'function') {

        return (...args) => {

            const retourMethod = kotlinInstance.apply(null, args)
            if(autoProxyMethod)
            return kotlinProxy(retourMethod, goDeep, autoProxyMethod);
            else return retourMethod;

        }
    } else if (typeof kotlinInstance !== 'object') {
        return kotlinInstance
    } else {
        let className = get(kotlinInstance, '__proto__.constructor.name');

        if (className === 'ArrayList') {
            let arrayName = Object.getOwnPropertyNames(kotlinInstance)
                .filter((itemArray) => {
                    return itemArray.startsWith("array")
                })[0];
            return kotlinInstance[arrayName].map((item) => {
                if (goDeep) {
                    return kotlinProxy(item, goDeep, autoProxyMethod)
                } else {
                    return item;

                }
            })
        } else {

            let newkotlinInstance = {};
            Object.getOwnPropertyNames(kotlinInstance).forEach(
                (oldName) => {
                    let newName = oldName.replace(/\_\S*\$/, "").replace(/\_\d/, "");
                    let propertyclassName = get(kotlinInstance, oldName + '.__proto__.constructor.name');
                    if (propertyclassName === 'ArrayList') {
                        newName += "Array"
                    }
                    let descriptor = Object.getOwnPropertyDescriptor(newkotlinInstance, newName);
                    if (!descriptor && !kotlinInstance.__proto__[newName]) {
                        try {

                            if (goDeep || propertyclassName === 'ArrayList') {
                                newkotlinInstance[newName] = kotlinProxy(kotlinInstance[oldName], goDeep, autoProxyMethod)
                            } else {
                                newkotlinInstance[newName] = kotlinInstance[oldName]
                            }
                        } catch (e) {
                            console.error(e)
                        }

                    }

                }
            );
            return newkotlinInstance
        }
    }
    return kotlinInstance;
}

export function printProxyModel(obj, indentation = "") {

    return Object.keys(obj).map(key => {

        if (key.endsWith("Array")) {
            return printProxyModel(obj[key][0]);
        }
        return key;

    })
}

