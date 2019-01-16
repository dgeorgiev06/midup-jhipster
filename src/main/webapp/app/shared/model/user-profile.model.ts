import { IFriend } from 'app/shared/model//friend.model';
import { IEvent } from 'app/shared/model//event.model';
import { IInvitee } from 'app/shared/model//invitee.model';

export interface IUserProfile {
  id?: number;
  imageUrl?: string;
  address?: string;
  addressLongitude?: number;
  addressLatitude?: number;
  userLogin?: string;
  userId?: number;
  requestingFriends?: IFriend[];
  acceptingFriends?: IFriend[];
  events?: IEvent[];
  invitees?: IInvitee[];
}

export const defaultValue: Readonly<IUserProfile> = {};
