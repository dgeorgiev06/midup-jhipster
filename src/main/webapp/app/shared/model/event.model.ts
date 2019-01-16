import { Moment } from 'moment';
import { IInvitee } from 'app/shared/model//invitee.model';

export interface IEvent {
  id?: number;
  eventName?: string;
  eventDate?: Moment;
  venueId?: string;
  address?: string;
  venueName?: string;
  imageUrl?: string;
  eventOrganizerId?: number;
  invitees?: IInvitee[];
}

export const defaultValue: Readonly<IEvent> = {};
