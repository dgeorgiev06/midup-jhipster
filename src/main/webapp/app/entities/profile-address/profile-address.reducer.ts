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

import { IProfileAddress, defaultValue } from 'app/shared/model/profile-address.model';

export const ACTION_TYPES = {
  SEARCH_PROFILEADDRESSES: 'profileAddress/SEARCH_PROFILEADDRESSES',
  FETCH_PROFILEADDRESS_LIST: 'profileAddress/FETCH_PROFILEADDRESS_LIST',
  FETCH_PROFILEADDRESS: 'profileAddress/FETCH_PROFILEADDRESS',
  CREATE_PROFILEADDRESS: 'profileAddress/CREATE_PROFILEADDRESS',
  UPDATE_PROFILEADDRESS: 'profileAddress/UPDATE_PROFILEADDRESS',
  DELETE_PROFILEADDRESS: 'profileAddress/DELETE_PROFILEADDRESS',
  RESET: 'profileAddress/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IProfileAddress>,
  entity: defaultValue,
  links: { next: 0 },
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type ProfileAddressState = Readonly<typeof initialState>;

// Reducer

export default (state: ProfileAddressState = initialState, action): ProfileAddressState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_PROFILEADDRESSES):
    case REQUEST(ACTION_TYPES.FETCH_PROFILEADDRESS_LIST):
    case REQUEST(ACTION_TYPES.FETCH_PROFILEADDRESS):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_PROFILEADDRESS):
    case REQUEST(ACTION_TYPES.UPDATE_PROFILEADDRESS):
    case REQUEST(ACTION_TYPES.DELETE_PROFILEADDRESS):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_PROFILEADDRESSES):
    case FAILURE(ACTION_TYPES.FETCH_PROFILEADDRESS_LIST):
    case FAILURE(ACTION_TYPES.FETCH_PROFILEADDRESS):
    case FAILURE(ACTION_TYPES.CREATE_PROFILEADDRESS):
    case FAILURE(ACTION_TYPES.UPDATE_PROFILEADDRESS):
    case FAILURE(ACTION_TYPES.DELETE_PROFILEADDRESS):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_PROFILEADDRESSES):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_PROFILEADDRESS_LIST):
      const links = parseHeaderForLinks(action.payload.headers.link);
      return {
        ...state,
        links,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: loadMoreDataWhenScrolled(state.entities, action.payload.data, links)
      };
    case SUCCESS(ACTION_TYPES.FETCH_PROFILEADDRESS):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_PROFILEADDRESS):
    case SUCCESS(ACTION_TYPES.UPDATE_PROFILEADDRESS):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_PROFILEADDRESS):
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

const apiUrl = 'api/profile-addresses';
const apiSearchUrl = 'api/_search/profile-addresses';

// Actions

export const getSearchEntities: ICrudSearchAction<IProfileAddress> = query => ({
  type: ACTION_TYPES.SEARCH_PROFILEADDRESSES,
  payload: axios.get<IProfileAddress>(`${apiSearchUrl}?query=` + query)
});

export const getEntities: ICrudGetAllAction<IProfileAddress> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_PROFILEADDRESS_LIST,
    payload: axios.get<IProfileAddress>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IProfileAddress> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_PROFILEADDRESS,
    payload: axios.get<IProfileAddress>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IProfileAddress> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_PROFILEADDRESS,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const updateEntity: ICrudPutAction<IProfileAddress> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_PROFILEADDRESS,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IProfileAddress> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_PROFILEADDRESS,
    payload: axios.delete(requestUrl)
  });
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
