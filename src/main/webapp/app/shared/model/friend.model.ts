import { Moment } from 'moment';

export interface IFriend {
  id?: number;
  status?: string;
  friendshipRequestDate?: Moment;
  friendshipStartDate?: Moment;
  friendRequestingId?: number;
  friendAcceptingId?: number;
}

export const defaultValue: Readonly<IFriend> = {};
