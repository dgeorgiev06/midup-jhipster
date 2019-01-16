import axios from 'axios';
import {
  ICrudSearchAction,
  parseHeaderForLinks,
  loadMoreDataWhenScrolled,
  ICrudGetAction,
  ICrudGetAllAction,
  ICrudPutAction,
  ICrudDeleteAction
} from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IFriend, defaultValue } from 'app/shared/model/friend.model';

export const ACTION_TYPES = {
  SEARCH_FRIENDS: 'friend/SEARCH_FRIENDS',
  FETCH_FRIEND_LIST: 'friend/FETCH_FRIEND_LIST',
  FETCH_FRIEND: 'friend/FETCH_FRIEND',
  CREATE_FRIEND: 'friend/CREATE_FRIEND',
  UPDATE_FRIEND: 'friend/UPDATE_FRIEND',
  DELETE_FRIEND: 'friend/DELETE_FRIEND',
  RESET: 'friend/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IFriend>,
  entity: defaultValue,
  links: { next: 0 },
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type FriendState = Readonly<typeof initialState>;

// Reducer

export default (state: FriendState = initialState, action): FriendState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_FRIENDS):
    case REQUEST(ACTION_TYPES.FETCH_FRIEND_LIST):
    case REQUEST(ACTION_TYPES.FETCH_FRIEND):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_FRIEND):
    case REQUEST(ACTION_TYPES.UPDATE_FRIEND):
    case REQUEST(ACTION_TYPES.DELETE_FRIEND):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_FRIENDS):
    case FAILURE(ACTION_TYPES.FETCH_FRIEND_LIST):
    case FAILURE(ACTION_TYPES.FETCH_FRIEND):
    case FAILURE(ACTION_TYPES.CREATE_FRIEND):
    case FAILURE(ACTION_TYPES.UPDATE_FRIEND):
    case FAILURE(ACTION_TYPES.DELETE_FRIEND):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_FRIENDS):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_FRIEND_LIST):
      const links = parseHeaderForLinks(action.payload.headers.link);
      return {
        ...state,
        links,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: loadMoreDataWhenScrolled(state.entities, action.payload.data, links)
      };
    case SUCCESS(ACTION_TYPES.FETCH_FRIEND):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_FRIEND):
    case SUCCESS(ACTION_TYPES.UPDATE_FRIEND):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_FRIEND):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {}
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState
      };
    default:
      return state;
  }
};

const apiUrl = 'api/friends';
const apiSearchUrl = 'api/_search/friends';

// Actions

export const getSearchEntities: ICrudSearchAction<IFriend> = query => ({
  type: ACTION_TYPES.SEARCH_FRIENDS,
  payload: axios.get<IFriend>(`${apiSearchUrl}?query=` + query)
});

export const getEntities: ICrudGetAllAction<IFriend> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_FRIEND_LIST,
    payload: axios.get<IFriend>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IFriend> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_FRIEND,
    payload: axios.get<IFriend>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IFriend> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_FRIEND,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const updateEntity: ICrudPutAction<IFriend> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_FRIEND,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IFriend> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_FRIEND,
    payload: axios.delete(requestUrl)
  });
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
