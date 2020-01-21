import {ACTION_TYPES} from "./services/actions";
import set from 'lodash/set'
import cloneDeep from 'lodash/cloneDeep'

export default function reducer(oldState, action) {

    if (action.type == ACTION_TYPES.UPDATE_STATE_TYPE) {
        let newsss = cloneDeep(oldState);
        set(newsss, action.payload.statePath, action.payload.data)
        return newsss
    }

    if (action.type == ACTION_TYPES.SELECT_OBJECT) {
        let newsss = cloneDeep(oldState);
        set(newsss, action.payload.statePath, action.payload.data)
        return newsss
    }

    return {...oldState}
}
