import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './event.reducer';
import { IEvent } from 'app/shared/model/event.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IEventDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class EventDetail extends React.Component<IEventDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { eventEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            Event [<b>{eventEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="eventName">Event Name</span>
            </dt>
            <dd>{eventEntity.eventName}</dd>
            <dt>
              <span id="eventDate">Event Date</span>
            </dt>
            <dd>
              <TextFormat value={eventEntity.eventDate} type="date" format={APP_DATE_FORMAT} />
            </dd>
            <dt>
              <span id="venueId">Venue Id</span>
            </dt>
            <dd>{eventEntity.venueId}</dd>
            <dt>
              <span id="address">Address</span>
            </dt>
            <dd>{eventEntity.address}</dd>
            <dt>
              <span id="venueName">Venue Name</span>
            </dt>
            <dd>{eventEntity.venueName}</dd>
            <dt>
              <span id="imageUrl">Image Url</span>
            </dt>
            <dd>{eventEntity.imageUrl}</dd>
            <dt>Event Organizer</dt>
            <dd>{eventEntity.eventOrganizerId ? eventEntity.eventOrganizerId : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/event" replace color="info">
            <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/event/${eventEntity.id}/edit`} replace color="primary">
            <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
          </Button>
        </Col>
      </Row>
    );
  }
}

const mapStateToProps = ({ event }: IRootState) => ({
  eventEntity: event.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(EventDetail);
