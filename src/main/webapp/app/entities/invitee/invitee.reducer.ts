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

import { IInvitee, defaultValue } from 'app/shared/model/invitee.model';

export const ACTION_TYPES = {
  SEARCH_INVITEES: 'invitee/SEARCH_INVITEES',
  FETCH_INVITEE_LIST: 'invitee/FETCH_INVITEE_LIST',
  FETCH_INVITEE: 'invitee/FETCH_INVITEE',
  CREATE_INVITEE: 'invitee/CREATE_INVITEE',
  UPDATE_INVITEE: 'invitee/UPDATE_INVITEE',
  DELETE_INVITEE: 'invitee/DELETE_INVITEE',
  RESET: 'invitee/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IInvitee>,
  entity: defaultValue,
  links: { next: 0 },
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type InviteeState = Readonly<typeof initialState>;

// Reducer

export default (state: InviteeState = initialState, action): InviteeState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_INVITEES):
    case REQUEST(ACTION_TYPES.FETCH_INVITEE_LIST):
    case REQUEST(ACTION_TYPES.FETCH_INVITEE):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_INVITEE):
    case REQUEST(ACTION_TYPES.UPDATE_INVITEE):
    case REQUEST(ACTION_TYPES.DELETE_INVITEE):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_INVITEES):
    case FAILURE(ACTION_TYPES.FETCH_INVITEE_LIST):
    case FAILURE(ACTION_TYPES.FETCH_INVITEE):
    case FAILURE(ACTION_TYPES.CREATE_INVITEE):
    case FAILURE(ACTION_TYPES.UPDATE_INVITEE):
    case FAILURE(ACTION_TYPES.DELETE_INVITEE):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_INVITEES):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_INVITEE_LIST):
      const links = parseHeaderForLinks(action.payload.headers.link);
      return {
        ...state,
        links,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: loadMoreDataWhenScrolled(state.entities, action.payload.data, links)
      };
    case SUCCESS(ACTION_TYPES.FETCH_INVITEE):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_INVITEE):
    case SUCCESS(ACTION_TYPES.UPDATE_INVITEE):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_INVITEE):
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

const apiUrl = 'api/invitees';
const apiSearchUrl = 'api/_search/invitees';

// Actions

export const getSearchEntities: ICrudSearchAction<IInvitee> = query => ({
  type: ACTION_TYPES.SEARCH_INVITEES,
  payload: axios.get<IInvitee>(`${apiSearchUrl}?query=` + query)
});

export const getEntities: ICrudGetAllAction<IInvitee> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_INVITEE_LIST,
    payload: axios.get<IInvitee>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<IInvitee> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_INVITEE,
    payload: axios.get<IInvitee>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IInvitee> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_INVITEE,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const updateEntity: ICrudPutAction<IInvitee> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_INVITEE,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IInvitee> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_INVITEE,
    payload: axios.delete(requestUrl)
  });
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
