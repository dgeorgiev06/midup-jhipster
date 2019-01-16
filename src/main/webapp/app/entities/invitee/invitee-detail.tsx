import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './invitee.reducer';
import { IInvitee } from 'app/shared/model/invitee.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IInviteeDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class InviteeDetail extends React.Component<IInviteeDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { inviteeEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            Invitee [<b>{inviteeEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="status">Status</span>
            </dt>
            <dd>{inviteeEntity.status}</dd>
            <dt>User Profile</dt>
            <dd>{inviteeEntity.userProfileId ? inviteeEntity.userProfileId : ''}</dd>
            <dt>Event</dt>
            <dd>{inviteeEntity.eventEventName ? inviteeEntity.eventEventName : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/invitee" replace color="info">
            <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/invitee/${inviteeEntity.id}/edit`} replace color="primary">
            <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
          </Button>
        </Col>
      </Row>
    );
  }
}

const mapStateToProps = ({ invitee }: IRootState) => ({
  inviteeEntity: invitee.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(InviteeDetail);
