import get from 'lodash/get';

export function kotlinProxy(kotlinInstance, goDeep = true) {
    if (kotlinInstance === undefined || kotlinInstance === null) {
        return kotlinInstance
    } else if (typeof kotlinInstance === 'function') {

        return (...args) => {

            const retourMethod = kotlinInstance.apply(null, args)
            return kotlinProxy(retourMethod, goDeep);

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
                    return kotlinProxy(item, goDeep)
                } else {
                    return item;

                }
            })
        } else {

            let newkotlinInstance = {}

            console.log(Object.getOwnPropertyNames(kotlinInstance))
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
                                newkotlinInstance[newName] = kotlinProxy(kotlinInstance[oldName], goDeep)
                            } else {
                                newkotlinInstance[newName] = kotlinInstance[oldName]
                            }
                        } catch (e) {
                            console.error(e)
                        }

                    }

                }
            )

            return newkotlinInstance
        }


    }

    return kotlinInstance;
}

